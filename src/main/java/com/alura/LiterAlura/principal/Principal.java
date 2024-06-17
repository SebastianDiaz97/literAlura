package com.alura.LiterAlura.principal;

import com.alura.LiterAlura.model.*;
import com.alura.LiterAlura.repository.AutorRepository;
import com.alura.LiterAlura.repository.LibroRepository;
import com.alura.LiterAlura.service.ConsumoAPI;
import com.alura.LiterAlura.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private ConsumoAPI consumoApi = new ConsumoAPI();
    ConvierteDatos conversor = new ConvierteDatos();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    Scanner teclado = new Scanner(System.in);
    String menu = """
            ------------------------------------------------
            Elija la opción a través de su número:
            1- buscar libro por titulo
            2- listar libros registrados
            3- listar autores registrados
            4- listar autores vivos en un determinado año
            5- listar libros por idioma
            6- top 10 libros más descargados
            7- buscar autor por nombre
            0- salir
            ------------------------------------------------""";

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }

    public void mostrarMenu() {

        var opcion = 1;


        do {
            System.out.println(menu);

            try {
                opcion = Integer.parseInt(teclado.nextLine());
            } catch (Exception e) {
                opcion = 7;
            }


            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                    listarAutoresPorAnio();
                    break;
                case 5:
                    listarPorIdioma();
                    break;
                case 6:
                    listarTop10();
                    break;
                case 7:
                    autorPorNombre();
                    break;
                case 8:
                    estadisticasLibros();
                    break;
                case 0:
                    opcion = 0;
                    break;
                default:
                    System.out.println("elija una opcion valida");
            }
        } while (opcion != 0);
    }

    private void estadisticasLibros() {
        List<Libro> libros = libroRepository.findAll();
        DoubleSummaryStatistics est = libros.stream()
                .filter(d -> d.getDownloadCount() > 0)
                .collect(Collectors.summarizingDouble(Libro::getDownloadCount));

        System.out.println("Total de libros: " + est.getCount());
        System.out.println("\nLibro con más descargas: ");
        libros.stream()
                .filter(l-> l.getDownloadCount() == est.getMax())
                .forEach(l -> System.out.println(l.getTitle() + " - " + est.getMax()));
        System.out.println("\nLibro con menos descargas: ");
        libros.stream()
                .filter(l-> l.getDownloadCount() == est.getMin())
                .forEach(l -> System.out.println(l.getTitle() + " - " + est.getMin()));
    }

    private void autorPorNombre() {
        System.out.println("Ingrese el nombre del autor que desea buscar");
        var nombreAutor = teclado.nextLine();
        Autor autor = autorRepository.buscarAutor(nombreAutor);
        if (autor != null){
            textoAutor(autor);
        }else {
            System.out.println("No hay autores que coincidan con la busqueda.");
        }
    }

    private void listarTop10() {
        List<Libro> topLibros = libroRepository.findTop10ByOrderByDownloadCountDesc();
        if (!topLibros.isEmpty()){
            topLibros.forEach(this::textoLibro);
        }else {
            System.out.println("Aún no hay libros en la base de datos.");
        }

    }

    private void listarPorIdioma() {

        List<Libro> libros = libroRepository.findAll();
        if (!libros.isEmpty()){
            System.out.println("Ingrese el codigo del idioma para buscar los libros");
            textoLenguaje(libros);
            var idioma = teclado.nextLine();

            List<Libro> libroFiltro = libros.stream().filter(libro -> libro.getLanguages().contains(idioma)).toList();
            if (!libroFiltro.isEmpty()) {
                libroFiltro.forEach(this::textoLibro);
            } else {
                System.out.println("No hay libros que coincidan con la busqueda.");
            }
        }else {
            System.out.println("Aún no hay libros en la base de datos.");
        }

    }

    private void listarAutoresPorAnio() {
        System.out.println("Ingrese el año que desea buscar");
        var anio = Integer.parseInt(teclado.nextLine());
        List<Autor> autors = autorRepository.buscarPorAnio(anio);
        if (!autors.isEmpty()) {
            autors.forEach(this::textoAutor);
        } else {
            System.out.println("No hay autores que coincidan con la busqueda.");
        }

    }

    private void listarAutores() {
        List<Autor> autors = autorRepository.findAll();
        if (!autors.isEmpty()) {
            autors.forEach(this::textoAutor);
        } else {
            System.out.println("Aún no hay autores en la base de datos.");
        }
    }

    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();
        if (!libros.isEmpty()) {
            libros.forEach(this::textoLibro);
        } else {
            System.out.println("Aún no hay libros en la base de datos.");
        }

    }

    private void buscarLibro() {
        System.out.println("Ingrese el nombre del libro que desea buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "+"));
        var datos = conversor.obtenerDatos(json, Datos.class);

        if (!datos.results().isEmpty()) {
            DatosLibro datosLibro = datos.results().get(0);
            Libro libro = new Libro(datosLibro);
            if (libroRepository.buscarLibro(libro.getTitle()) == null) {
                List<Autor> autores = datosLibro.authors().stream()
                        .map(a -> new Autor(a))
                        .collect(Collectors.toList());
                List<Autor> autorList = new ArrayList<>();

                for (int i = 0; i < autores.toArray().length; i++) {
                    Autor autor = autorRepository.buscarAutor(autores.get(i).getName());
                    if (autor == null) {
                        autorRepository.save(autores.get(i));
                        autorList.add(autores.get(i));
                    } else {
                        autorList.add(autor);
                    }
                }


                libro.setAuthors(autorList);

                libroRepository.save(libro);
                textoLibro(libro);
            } else {
                System.out.println("libro ya ingresado");
            }

        } else {
            System.out.println("libro no encontrado");
        }

    }


    public void textoLibro(Libro libro) {
        var textoAutor = libro.getAuthors().stream().map(a -> a.getName()).collect(Collectors.joining(" - "));
        var textoLenguaje = libro.getLanguages().stream().collect(Collectors.joining(", "));
        System.out.printf("""
                \n----------LIBRO----------
                Título: %s
                Autor: %s
                Idioma: %s
                Número de descargas: %d
                """, libro.getTitle(), textoAutor, textoLenguaje, libro.getDownloadCount());
    }

    public void textoAutor(Autor autor) {
        List<Libro> libros = autor.getLibro();
        var textoLibros = libros.stream().map(a -> a.getTitle()).collect(Collectors.joining(" - "));
        System.out.printf("""
                \nAutor: %s
                Fecha de nacimiento: %s
                Fecha de fallecimiento: %s
                Libros: %s
                """, autor.getName(), autor.getBirth_year(), autor.getDeath_year(), textoLibros);
    }

    public void textoLenguaje(List<Libro> libros) {
        StringBuilder url = new StringBuilder("https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/iso-language-codes-639-1-and-639-2/" +
                "records?select=alpha2%2C%20english&&where=");
        ArrayList lenguaje = new ArrayList<>();
        libros.forEach(l -> lenguaje.addAll(l.getLanguages()));
        var lenguaje1 = lenguaje.stream().distinct().toList();
        for (int i = 0; i < lenguaje1.size(); i++) {
            if (i == 0) {
                url.append("alpha2%20%3D%20%27").append(lenguaje1.get(i)).append("%27");
            } else {
                url.append("%20or%20alpha2%20%3D%20%27").append(lenguaje1.get(i)).append("%27");
            }
        }
        var json = consumoApi.obtenerDatos(url.toString());
        var datos = conversor.obtenerDatos(json, DatosCodigo.class);
        datos.results().forEach(l->{
            System.out.printf("%s - %s\n",l.alpha2(), l.english().get(0));
        });

    }

}
