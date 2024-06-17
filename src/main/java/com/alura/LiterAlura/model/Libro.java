package com.alura.LiterAlura.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 //   @Column(unique = true)
    private String title;
    private Long idGuten;
    private List<String> languages;
    private int downloadCount;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "rel_books_auths",
            joinColumns = @JoinColumn(name = "FK_BOOK", nullable = false),
            inverseJoinColumns = @JoinColumn(name="FK_AUTHOR", nullable = false)
    )
    private List<Autor> authors;

    public Libro() {
    }

    public Libro(DatosLibro datosLibro) {
        this.title = datosLibro.title();
        this.idGuten = datosLibro.idGuten();
        this.downloadCount = datosLibro.download_count();
        this.languages = datosLibro.languages();
    }

    public List<Autor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Autor> authors) {
        this.authors = authors;
    }

    public Long getIdGuten() {
        return idGuten;
    }

    public void setIdGuten(Long idGuten) {
        this.idGuten = idGuten;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }
}
