/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Libro;
import ec.edu.ups.biblioteca.models.Prestamo;
import ec.edu.ups.biblioteca.models.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class PrestamoDAOMemoria implements PrestamoDAO {

    private List<Prestamo> prestamos;

    public PrestamoDAOMemoria() {
        prestamos = new ArrayList<>();
    }

    @Override
    public void crearPrestamo(Prestamo prestamo) {
        prestamos.add(prestamo);
    }

    @Override
    public Prestamo buscarPrestamo(String isbn) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLibro().getIsbn().equals(isbn) && !prestamo.isDevuelto()) {
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

    @Override
    public boolean tienePrestamoActivo(Libro libro) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLibro().equals(libro) && !prestamo.isDevuelto()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean tienePrestamoActivo(Usuario usuario) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getUsuario().equals(usuario) && !prestamo.isDevuelto()) {
                return true;
            }
        }
        return false;
    }
}
