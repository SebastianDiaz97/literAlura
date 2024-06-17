package com.alura.LiterAlura.repository;

import com.alura.LiterAlura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository  extends JpaRepository<Autor, Long> {
    @Query("select a from Autor a where a.name ilike %:autor%")
    Autor buscarAutor(String autor);

    @Query("select a from Autor a where :anio between a.birth_year and a.death_year")
    List<Autor> buscarPorAnio(int anio);
}
