/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Autor;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author DELL
 */
public class AutorDAOMemoria implements AutorDAO  {
    private List<Autor> autores;

    public AutorDAOMemoria() {
        autores = new ArrayList<>();
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
}
