/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.models;

import ec.edu.ups.biblioteca.enums.CategoriaLibro;
import java.util.Objects;

/**
 *
 * @author DELL
 */
public class Libro {

    private String isbn;
    private String titulo;
    private Autor autor;
    private int anio;
    private CategoriaLibro categoria;
    private boolean disponible;

    public Libro() {
        this.disponible = true;
    }

    public Libro(String isbn, String titulo, Autor autor, int anio, CategoriaLibro categoria) {
        this(isbn, titulo, autor, anio, categoria, true);
    }

    public Libro(String isbn, String titulo, Autor autor, int anio, CategoriaLibro categoria, boolean disponible) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anio = anio;
        this.categoria = categoria;
        this.disponible = disponible;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public CategoriaLibro getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaLibro categoria) {
        this.categoria = categoria;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Libro other = (Libro) obj;
        return Objects.equals(this.isbn, other.isbn);
    }

    @Override
    public String toString() {
        return titulo;
    }

}
