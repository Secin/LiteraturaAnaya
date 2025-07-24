package com.aluracursos.LiteraturaAnaya.DTO;

import com.aluracursos.LiteraturaAnaya.Model.Autor;
import com.aluracursos.LiteraturaAnaya.Model.Libro;

import java.util.List;
import java.util.stream.Collectors;

public class LibroDTO {
    private String titulo;
    private String idiomas;
    private Integer numeroDescargas;
    private String autores;

    public LibroDTO(Libro libro) {
        this.titulo = libro.getTitulo();
        // Convierte lista de idiomas a una sola cadena separada por coma
        this.idiomas = String.join(", ", libro.getIdiomas());
        this.numeroDescargas = libro.getNumeroDeDescargas();
        // Convierte lista de autores a cadena
        this.autores = libro.getAutores() != null
                ? libro.getAutores().stream()
                .map(Autor::getNombre)
                .collect(Collectors.joining(", "))
                :"Sin autores";
    }

    @Override
    public String toString() {
        return "Título: " + titulo +
                "\nIdiomas: " + String.join(", ", idiomas) +
                "\nNúmero de Descargas: " + numeroDescargas +
                "\nAutores: " + String.join(", ", autores) +
                "\n-----------------------------";
    }

}
