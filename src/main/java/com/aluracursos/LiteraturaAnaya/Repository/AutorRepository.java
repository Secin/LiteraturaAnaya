package com.aluracursos.LiteraturaAnaya.Repository;

import com.aluracursos.LiteraturaAnaya.Model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository  extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombre(String nombre);
    Optional<Autor> findByNombreIgnoreCase(String nombre);
    @Query("SELECT a FROM Autor a WHERE a.fechaDeNacimiento <= :anio AND (a.fechaDeMuerte IS NULL OR a.fechaDeMuerte > :anio)")
    List<Autor> buscarAutoresVivosEnAnio(int anio);
}
