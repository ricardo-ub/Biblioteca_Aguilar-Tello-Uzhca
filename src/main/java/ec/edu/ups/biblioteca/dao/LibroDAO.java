/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Libro;
import java.util.List;


/**
 *
 * @author DELL
 */
public interface LibroDAO {
    void crearLibro(Libro libro);

    Libro buscarLibro(String isbn);

    void actualizarLibro(Libro libro);

    boolean eliminarLibro(String isbn);

    List<Libro> listarLibros();
}
