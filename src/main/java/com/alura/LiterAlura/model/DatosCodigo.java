package com.alura.LiterAlura.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosCodigo(
        List<DatosCodigoFinal> results
) {
}
