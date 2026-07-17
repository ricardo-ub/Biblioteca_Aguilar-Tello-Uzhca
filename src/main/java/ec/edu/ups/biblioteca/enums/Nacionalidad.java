/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package ec.edu.ups.biblioteca.enums;

/**
 *
 * @author USER
 */
public enum Nacionalidad {

    ECUATORIANA("Ecuatoriana"),
    COLOMBIANA("Colombiana"),
    PERUANA("Peruana"),
    VENEZOLANA("Venezolana"),
    CHILENA("Chilena"),
    ARGENTINA("Argentina"),
    BOLIVIANA("Boliviana"),
    BRASILENA("Brasileña"),
    MEXICANA("Mexicana"),
    ESPANOLA("Española"),
    ESTADOUNIDENSE("Estadounidense"),
    OTRA("Otra");

    private String nombre;

    private Nacionalidad(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
