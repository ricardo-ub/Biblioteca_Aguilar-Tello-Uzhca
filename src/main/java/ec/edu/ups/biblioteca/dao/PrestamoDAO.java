/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Libro;
import ec.edu.ups.biblioteca.models.Prestamo;
import ec.edu.ups.biblioteca.models.Usuario;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface PrestamoDAO {
    void crearPrestamo(Prestamo prestamo);

    Prestamo buscarPrestamo(String isbn);

    void devolverLibro(String isbn);

    List<Prestamo> listarPrestamos();

    boolean tienePrestamoActivo(Libro libro);

    boolean tienePrestamoActivo(Usuario usuario);
    
    void eliminarPrestamosPorLibro(String isbn);
}
