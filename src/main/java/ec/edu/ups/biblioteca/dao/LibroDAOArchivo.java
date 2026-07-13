/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.enums.CategoriaLibro;
import ec.edu.ups.biblioteca.models.Autor;
import ec.edu.ups.biblioteca.models.Libro;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class LibroDAOArchivo implements LibroDAO {

    private String rutaArchivo;
    private AutorDAO autorDAO;

    public LibroDAOArchivo(AutorDAO autorDAO) {
        this.autorDAO = autorDAO;

        File carpeta = new File("archivos");

        if (carpeta.exists() == false) {
            carpeta.mkdir();
        }

        rutaArchivo = "archivos/libros.ups";
        File archivo = new File(rutaArchivo);

        try {
            if (archivo.exists() == false) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error al crear el archivo de libros");
        }
    }

    @Override
    public void crearLibro(Libro libro) {
        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "rw");
            archivo.seek(archivo.length());

            escribirCadena(archivo, libro.getIsbn());
            escribirCadena(archivo, libro.getTitulo());
            escribirCadena(archivo, libro.getAutor().getNombre());
            archivo.writeInt(libro.getAnio());
            escribirCadena(archivo, libro.getCategoria().name());
            archivo.writeBoolean(libro.isDisponible());

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de libros");
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo de libros");
        }
    }

    @Override
    public Libro buscarLibro(String isbn) {
        List<Libro> libros = listarLibros();

        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }

        return null;
    }

    @Override
    public void actualizarLibro(Libro libro) {
        List<Libro> libros = listarLibros();

        for (Libro libroExistente : libros) {
            if (libroExistente.getIsbn().equals(libro.getIsbn())) {
                libroExistente.setTitulo(libro.getTitulo());
                libroExistente.setAutor(libro.getAutor());
                libroExistente.setAnio(libro.getAnio());
                libroExistente.setCategoria(libro.getCategoria());
                libroExistente.setDisponible(libro.isDisponible());
                break;
            }
        }

        guardarLibros(libros);
    }

    @Override
    public boolean eliminarLibro(String isbn) {
        List<Libro> libros = listarLibros();
        Libro libroEliminar = null;

        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbn)) {
                libroEliminar = libro;
                break;
            }
        }

        if (libroEliminar == null) {
            return false;
        }

        libros.remove(libroEliminar);
        guardarLibros(libros);
        return true;
    }

    @Override
    public List<Libro> listarLibros() {
        List<Libro> libros = new ArrayList<>();

        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "r");

            while (archivo.getFilePointer() < archivo.length()) {
                String isbn = leerCadena(archivo);
                String titulo = leerCadena(archivo);
                String nombreAutor = leerCadena(archivo);
                int anio = archivo.readInt();
                String nombreCategoria = leerCadena(archivo);
                boolean disponible = archivo.readBoolean();

                Autor autor = autorDAO.buscarAutor(nombreAutor);
                CategoriaLibro categoria = CategoriaLibro.valueOf(nombreCategoria);
                Libro libro = new Libro(isbn, titulo, autor, anio, categoria, disponible);
                libros.add(libro);
            }

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de libros");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de libros");
        }

        return libros;
    }

    private void guardarLibros(List<Libro> libros) {
        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "rw");
            archivo.setLength(0);

            for (Libro libro : libros) {
                escribirCadena(archivo, libro.getIsbn());
                escribirCadena(archivo, libro.getTitulo());
                escribirCadena(archivo, libro.getAutor().getNombre());
                archivo.writeInt(libro.getAnio());
                escribirCadena(archivo, libro.getCategoria().name());
                archivo.writeBoolean(libro.isDisponible());
            }

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de libros");
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo de libros");
        }
    }

    private void escribirCadena(RandomAccessFile archivo, String cadena) throws IOException {
        archivo.writeInt(cadena.length());
        archivo.writeChars(cadena);
    }

    private String leerCadena(RandomAccessFile archivo) throws IOException {
        int longitud = archivo.readInt();
        String cadena = "";

        for (int i = 0; i < longitud; i++) {
            cadena += archivo.readChar();
        }

        return cadena;
    }
}
