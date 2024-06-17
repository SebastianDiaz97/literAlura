package com.alura.LiterAlura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
        String title,
        @JsonAlias("id") Long idGuten,
        List<DatosAutor> authors,
        List<String> languages,
        int download_count
){

}
