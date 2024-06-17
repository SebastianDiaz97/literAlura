package com.alura.LiterAlura.repository;

import com.alura.LiterAlura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    @Query("select l from Libro l where l.title ilike %:libro%")
    Libro buscarLibro(String libro);

    List<Libro> findTop10ByOrderByDownloadCountDesc();

    Libro findByDownloadCount(int dowload);

}
