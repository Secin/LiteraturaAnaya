package com.aluracursos.LiteraturaAnaya.Repository;

import com.aluracursos.LiteraturaAnaya.Model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepository  extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloContainsIgnoreCase(String nombreTitulo);

    @Query(value = "SELECT * FROM libros ORDER BY numero_de_descargas DESC LIMIT 6", nativeQuery = true)
    List<Libro> top10LibrosMasDescargados();

    @Query("SELECT l FROM Libro l JOIN l.idiomas i WHERE i = :idioma")
    List<Libro> findByContainingIdioma(@Param("idioma") String idioma);

}
