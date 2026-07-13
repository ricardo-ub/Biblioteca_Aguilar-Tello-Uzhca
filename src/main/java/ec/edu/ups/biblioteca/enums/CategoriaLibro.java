/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package ec.edu.ups.biblioteca.enums;

/**
 *
 * @author User
 */
public enum CategoriaLibro {
    
    NOVELA("categoria.novela"),
    CIENCIA("categoria.ciencia"),
    HISTORIA("categoria.historia"),
    TECNOLOGIA("categoria.tecnologia"),
    PROGRAMACION("categoria.programacion"),
    MATEMATICAS("categoria.matematicas"),
    INFANTIL("categoria.infantil");

    private final String claveMensaje;

    private CategoriaLibro(String claveMensaje) {
        this.claveMensaje = claveMensaje;
    }

    public String getClaveMensaje() {
        return claveMensaje;
    }
}
