/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Autor;
import ec.edu.ups.biblioteca.models.Libro;
import ec.edu.ups.biblioteca.models.Prestamo;
import ec.edu.ups.biblioteca.models.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class BibliotecaDAOMemoria implements BibliotecaDAO {

    private List<Autor> autores;
    private List<Libro> libros;
    private List<Usuario> usuarios;
    private List<Prestamo> prestamos;

    public BibliotecaDAOMemoria() {
        autores = new ArrayList<>();
        libros = new ArrayList<>();
        usuarios = new ArrayList<>();
        prestamos = new ArrayList<>();

    }

    @Override
    public void crearAutor(Autor autor) {
        autores.add(autor);
    }

    @Override
    public Autor buscarAutor(String nombre) {
        for (Autor autor : autores) {
            if (autor.getNombre().equalsIgnoreCase(nombre)) {
                return autor;
            }
        }
        return null;
    }

    @Override
    public void actualizarAutor(Autor autor) {
        Autor existente = buscarAutor(autor.getNombre());
        if (existente != null) {
            existente.setNacionalidad(autor.getNacionalidad());
            existente.setCorreo(autor.getCorreo());
        }
    }

    @Override
    public List<Autor> listarAutores() {
        return autores;
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
        }
    }

    @Override
    public boolean eliminarLibro(String isbn) {
        Libro libro = buscarLibro(isbn);
        if (libro == null) {
            return false;
        }
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLibro().equals(libro) && !prestamo.isDevuelto()) {
                return false;
            }
        }
        libros.remove(libro);
        return true;
    }

    @Override
    public List<Libro> listarLibros() {
        return libros;
    }

    @Override
    public void crearUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    @Override
    public Usuario buscarUsuario(String cedula) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCedula().equals(cedula)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {
        Usuario existente = buscarUsuario(usuario.getCedula());
        if (existente != null) {
            existente.setNombre(usuario.getNombre());
            existente.setCorreo(usuario.getCorreo());
        }
    }

    @Override
    public boolean eliminarUsuario(String cedula) {
        Usuario usuario = buscarUsuario(cedula);
        if (usuario == null) {
            return false;
        }
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getUsuario().equals(usuario) && !prestamo.isDevuelto()) {
                return false;
            }
        }
        usuarios.remove(usuario);
        return true;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    @Override
    public void crearPrestamo(Prestamo prestamo) {
        prestamos.add(prestamo);
    }

    @Override
    public Prestamo buscarPrestamo(String isbn) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLibro().getIsbn().equals(isbn)
                    && !prestamo.isDevuelto()) {
                return prestamo;
            }
        }
        return null;
    }

    @Override
    public void devolverLibro(String isbn) {
        Prestamo prestamo = buscarPrestamo(isbn);
        if (prestamo != null) {
            prestamo.setDevuelto(true);
        }
    }

    @Override
    public List<Prestamo> listarPrestamos() {
        return prestamos;
    }
}