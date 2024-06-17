package com.alura.LiterAlura.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int birth_year;
    private int death_year;
   // @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.EAGER)
    private List<Libro> libro;

    public Autor() {
    }

    public Autor(DatosAutor d) {
        this.birth_year = d.birth_year();
        this.death_year = d.death_year();
        this.name = d.name();
    }


    public List<Libro> getLibro() {
        return libro;
    }

    public void setLibro(List<Libro> libro) {
        this.libro = libro;
    }

    public int getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(int birth_year) {
        this.birth_year = birth_year;
    }

    public int getDeath_year() {
        return death_year;
    }

    public void setDeath_year(int death_year) {
        this.death_year = death_year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
