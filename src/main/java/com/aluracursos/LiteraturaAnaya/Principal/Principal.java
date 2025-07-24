package com.aluracursos.LiteraturaAnaya.Principal;

import com.aluracursos.LiteraturaAnaya.DTO.LibroDTO;
import com.aluracursos.LiteraturaAnaya.Model.Autor;
import com.aluracursos.LiteraturaAnaya.Model.Datos;
import com.aluracursos.LiteraturaAnaya.Model.DatosLibros;
import com.aluracursos.LiteraturaAnaya.Model.Libro;
import com.aluracursos.LiteraturaAnaya.Repository.AutorRepository;
import com.aluracursos.LiteraturaAnaya.Repository.LibroRepository;
import com.aluracursos.LiteraturaAnaya.Service.ConsumoAPI;
import com.aluracursos.LiteraturaAnaya.Service.ConvierteDatos;
import com.google.gson.Gson;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private List<DatosLibros> datosLibros = new ArrayList<>();
    private LibroRepository libroRepo;
    private AutorRepository autorRepo;
    private List<Libro> libros;
    private Optional<Libro> libroBuscado;

    public Principal(LibroRepository librorepo, AutorRepository autorrepo) {
        this.libroRepo = librorepo;
        this.autorRepo = autorrepo;
    }

    public void muestraElMenu() {
        var opcion = -1;


        while (opcion != 0) {
            var menu = """
                    ==============================================================
                                   MENÚ DE OPCIONES -  APP DE LITERATURA
                    ==============================================================
                    Elija la opción a tráves de su número:
                    1 - Buscar Libro Por titulo
                    2 - Mostrar los Libros buscados
                    3 - Buscar Libro por titulo en BD
                    4 - Lista los Libros Registrados
                    5 - Lista los Autores Registrados
                    6 - Los 6 Libros mas descargados
                    7 - Buscar un Libros por su Idioma
                    8 - Lista de Autores vivos en un año determinado
                    0 - Salir
                    --------------------------------------------------------------
                    Elija una opción:
                    """;

            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> buscarLibroPorTitulo();
                case 2 -> mostrarLibrosBuscados();
                case 3 -> buscarlibroTituloBD();
                case 4 -> listarLibrosEnBD();
                case 5 -> listarAutoresEnBD();
                case 6 -> top10LibosMasDescarados();
                case 7 ->  buscarLibrosPorIdiomaBD();
                case 8 -> buscarAutoresVivosPorAnio();
                case 0 -> System.out.println("Saliendo de la aplicación...");
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private DatosLibros getDatosLibros() {
        System.out.println("Escribe el título del libro que deseas buscar:");
        String tituloNombre = teclado.nextLine();
        String url = URL_BASE + "?search=" + tituloNombre.replace(" ", "+");
        String json = consumoAPI.obtenerDatos(url);
        System.out.println(json);
        Datos datos = conversor.obtenerDatos(json, Datos.class);

        Gson gson = new Gson();
        DatosLibros datosLibros=gson.fromJson(json, DatosLibros.class);
        System.out.println("Los datos son:"+datosLibros);

        if (datos.resultados().isEmpty()) {
            System.out.println("No se encontró ningún libro con ese título.");
            return null;
        }
        System.out.println("Libro encontrado en Gutendex es: "+datos.resultados());
        return datos.resultados().get(0); // Devolver solo el primer libro encontrado
    }

    private void buscarLibroPorTitulo() {
        DatosLibros datos = getDatosLibros();
        if (datos == null) return;
        Optional<Libro> libroExistente = libroRepo.findByTituloContainsIgnoreCase(datos.titulo());
        if (libroExistente.isPresent()) {
            System.out.println("El libro \"" + datos.titulo() + "\" ya está almacenado en la base de datos.");
            System.out.println(libroExistente.get());
        } else {
            Libro libro = new Libro(datos);
            libroRepo.save(libro);
            if (datos.idiomas() != null) {
                List<String> idiomasNormalizados = datos.idiomas().stream()
                        .map(String::toLowerCase)
                        .distinct()
                        .collect(Collectors.toList());
                libro.setIdiomas(idiomasNormalizados);
            }
            if(datos.autor()!= null ){
                List<Autor> autores = datos.autor().stream()
                        .map(datoAutor -> autorRepo
                                .findByNombre(datoAutor.nombre())
                                .orElseGet(() -> autorRepo.save(new Autor(datoAutor)))
                        )
                        .collect(Collectors.toList());
                libro.setAutores(autores);
                autores.forEach(a -> a.setLibros(libro));
            }
            System.out.println("Libro guardado correctamente:");
            System.out.println(libro);
        }
    }

    private void mostrarLibrosBuscados() { List<Libro> libros = libroRepo.findAll();

        /*for (Libro libro : libros) {
            System.out.println("Libro: " + libro.getTitulo());
            System.out.println("Autores crudos: " + libro.getAutores());
        }*/
        if (libros.isEmpty()) {
            System.out.println("No hay libros guardados aún.");
            return;
        }

        List<LibroDTO> librosDTO = libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .map(LibroDTO::new)  // Convertir a DTO
                .collect(Collectors.toList());

        librosDTO.forEach(System.out::println); // Imprimir DTOs
    }



    private void buscarlibroTituloBD() {
        System.out.println("Escribe el título del libro que deseas buscar:");
        String nombreTitulo = teclado.nextLine();
        libroBuscado = libroRepo.findByTituloContainsIgnoreCase(nombreTitulo);
        if (libroBuscado.isPresent()) {
            System.out.println("Libro encontrado :" + libroBuscado.get());
        } else {
            System.out.println("No se encontró el libro en la base de datos.");
        }




    }

    private void listarLibrosEnBD() {
        List<Libro> libros = libroRepo.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
            return;
        }
        System.out.println("Se encontraron los siguientes libros:");
        libros.stream()
                .map(Libro::getTitulo)
                .forEach(titulo -> System.out.println("📖 " + titulo));
    }

    private void listarAutoresEnBD() {
        List<Autor> autores = autorRepo.findAll();
        if (autores.isEmpty()) {
            System.out.println(" No hay autores registrados en la base de datos.");
            return;
        }
        System.out.println("Se encontraron Los siguientes autores:");
        autores.stream()
                .map(Autor::getNombre)
                .forEach(nombre -> System.out.println("Nombre-Autor:" + nombre));

    }

    private void top10LibosMasDescarados() {
        List<Libro> top10 = libroRepo.top10LibrosMasDescargados();
        System.out.println("Top 10 libros más descargados:");
        top10.forEach(libro ->
                System.out.println(libro.getTitulo() + " - Descargas: " + libro.getNumeroDeDescargas()));
    }


    private void buscarAutoresVivosPorAnio() {
        System.out.println("Escribe el año para verificar qué autores estaban vivos:");
        int fecha = teclado.nextInt();
        teclado.nextLine();
        List<Autor> autoresVivos = autorRepo.buscarAutoresVivosEnAnio(fecha);
        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontró ningún autor vivo en ese año.");
        } else {
            System.out.println(" Autores vivos en el año " + fecha + ":");
            autoresVivos.forEach(a -> System.out.println(a.getNombre()));
        }
    }


    private void buscarLibrosPorIdiomaBD() {
        // Mapa de idiomas disponibles con su código y nombre
        Map<Integer, Map.Entry<String, String>> opcionesIdiomas = Map.of(
                1, Map.entry("en", "Inglés"),
                2, Map.entry("es", "Español"),
                3, Map.entry("fr", "Francés"),
                4, Map.entry("it", "Italiano"),
                5, Map.entry("zh", "Chino"),
                6, Map.entry("tl", "Talago")
        );

        // Mostrar menú de idiomas
        System.out.println("""
        Seleccione el idioma para buscar libros:
        1 - Inglés (en)
        2 - Español (es)
        3 - Francés (fr)
        4 - Italiano (it)
        5 - Chino (zh)
        6 - Talago (tl)
        0 - Cancelar
        """);

        System.out.print("Elija una opción: ");
        int opcion = teclado.nextInt();
        teclado.nextLine();

        if (opcion == 0) {
            System.out.println("Búsqueda cancelada.");
            return;
        }

        if (!opcionesIdiomas.containsKey(opcion)) {
            System.out.println("Opción no válida.");
            return;
        }

        Map.Entry<String, String> idiomaSeleccionado = opcionesIdiomas.get(opcion);
        String codigoIdioma = idiomaSeleccionado.getKey();
        String nombreIdioma = idiomaSeleccionado.getValue();

        // Buscar libros en ese idioma
        List<Libro> librosEnIdioma = libroRepo.findByContainingIdioma(codigoIdioma);

        if (librosEnIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en " + nombreIdioma + " (" + codigoIdioma + ")");
        } else {
            System.out.println("Libros disponibles en " + nombreIdioma + " (" + codigoIdioma + "):");
            librosEnIdioma.forEach(libro -> {
                System.out.println("Título: " + libro.getTitulo());
            });
        }
    }

}
