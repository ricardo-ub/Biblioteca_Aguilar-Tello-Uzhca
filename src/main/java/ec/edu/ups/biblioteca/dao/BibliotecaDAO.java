/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Autor;
import ec.edu.ups.biblioteca.models.Libro;
import ec.edu.ups.biblioteca.models.Prestamo;
import ec.edu.ups.biblioteca.models.Usuario;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface BibliotecaDAO {
    void crearAutor(Autor autor);
    Autor buscarAutor(String nombre);
    void actualizarAutor(Autor autor);
    List<Autor> listarAutores();

    void crearLibro(Libro libro);
    Libro buscarLibro(String isbn);
    void actualizarLibro(Libro libro);
    boolean eliminarLibro(String isbn);
    List<Libro> listarLibros();

    void crearUsuario(Usuario usuario);
    Usuario buscarUsuario(String cedula);
    void actualizarUsuario(Usuario usuario);
    boolean eliminarUsuario(String cedula);
    List<Usuario> listarUsuarios();

    void crearPrestamo(Prestamo prestamo);
    Prestamo buscarPrestamo(String isbn);
    void devolverLibro(String isbn);
    List<Prestamo> listarPrestamos();

}

