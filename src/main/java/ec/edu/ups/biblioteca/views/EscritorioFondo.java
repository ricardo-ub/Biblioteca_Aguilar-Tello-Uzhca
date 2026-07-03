/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.views;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;

/**
 *
 * @author Caloo
 */
public class EscritorioFondo extends JDesktopPane {

    private Image imagen;

    public EscritorioFondo() {
        try {
            imagen = new ImageIcon(getClass().getResource("/ec/edu/ups/biblioteca/images/Fondo JDesktop.png")).getImage();
        } catch (Exception e) {
            System.err.println("No se encontró la imagen de fondo.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }

}
