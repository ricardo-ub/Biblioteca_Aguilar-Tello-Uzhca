/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Autor;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface AutorDAO {
    void crearAutor(Autor autor);

    Autor buscarAutor(String nombre);

    void actualizarAutor(Autor autor);

    List<Autor> listarAutores();
}
