/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Libro;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class LibroDAOMemoria implements LibroDAO {

    private List<Libro> libros;

    public LibroDAOMemoria() {
        libros = new ArrayList<>();
    }

    @Override
    public void crearLibro(Libro libro) {
        libros.add(libro);
    }

    @Override
    public Libro buscarLibro(String isbn) {
        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }
        return null;
    }

    @Override
    public void actualizarLibro(Libro libro) {
        Libro existente = buscarLibro(libro.getIsbn());
        if (existente != null) {
            existente.setTitulo(libro.getTitulo());
            existente.setAutor(libro.getAutor());
            existente.setAnio(libro.getAnio());
            existente.setCategoria(libro.getCategoria());
            existente.setDisponible(libro.isDisponible());
        }
    }

    @Override
    public boolean eliminarLibro(String isbn) {
        Libro libro = buscarLibro(isbn);
        if (libro == null) {
            return false;
        }
        libros.remove(libro);
        return true;
    }

    @Override
    public List<Libro> listarLibros() {
        return libros;
    }
}
