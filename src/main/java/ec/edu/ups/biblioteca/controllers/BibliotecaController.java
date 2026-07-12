/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.controllers;

import ec.edu.ups.biblioteca.dao.AutorDAO;
import ec.edu.ups.biblioteca.dao.LibroDAO;
import ec.edu.ups.biblioteca.dao.PrestamoDAO;
import ec.edu.ups.biblioteca.dao.UsuarioDAO;
import ec.edu.ups.biblioteca.views.ActualizarRegistrarLibroView;
import ec.edu.ups.biblioteca.views.ActualizarRegistrarUsuarioView;
import ec.edu.ups.biblioteca.views.DevolucionLibroView;
import ec.edu.ups.biblioteca.views.ListaLibrosView;
import ec.edu.ups.biblioteca.views.ListaPrestamosView;
import ec.edu.ups.biblioteca.views.ListaUsuariosView;
import ec.edu.ups.biblioteca.views.RegistrarAutorView;
import ec.edu.ups.biblioteca.views.RegistrarLibroView;
import ec.edu.ups.biblioteca.views.RegistrarPrestamoView;
import ec.edu.ups.biblioteca.views.RegistrarUsuarioView;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author DELL
 */
public class BibliotecaController {

    private AutorController autorController;
    private LibroController libroController;
    private UsuarioController usuarioController;
    private PrestamoController prestamoController;

    private Locale localizacion;
    private ResourceBundle mensajes;

    public BibliotecaController(RegistrarAutorView registrarAutorView, RegistrarLibroView registrarLibroView,
            RegistrarUsuarioView registrarUsuarioView, ActualizarRegistrarLibroView actualizarRegistroLibroView,
            ActualizarRegistrarUsuarioView actualizarRegistroUsuarioView, RegistrarPrestamoView registrarPrestamoView,
            DevolucionLibroView devolucionLibroView, ListaLibrosView listaLibrosView,
            ListaUsuariosView listaUsuariosView, ListaPrestamosView listaPrestamosView,
            AutorDAO autorDAO, LibroDAO libroDAO, UsuarioDAO usuarioDAO, PrestamoDAO prestamoDAO) {

        this.localizacion = new Locale("es", "EC");
        this.mensajes = ResourceBundle.getBundle("ec.edu.ups.biblioteca.i18n.mensajes", localizacion);

        this.autorController = new AutorController(autorDAO, registrarAutorView, registrarLibroView,
                actualizarRegistroLibroView, mensajes);

        this.libroController = new LibroController(libroDAO, autorDAO, prestamoDAO, registrarLibroView,
                actualizarRegistroLibroView, registrarPrestamoView, listaLibrosView, mensajes);

        this.usuarioController = new UsuarioController(usuarioDAO, prestamoDAO, registrarUsuarioView,
                actualizarRegistroUsuarioView, registrarPrestamoView, listaUsuariosView, mensajes);

        this.prestamoController = new PrestamoController(libroDAO, usuarioDAO, prestamoDAO, registrarPrestamoView,
                devolucionLibroView, listaPrestamosView, mensajes, localizacion);

        //Para que al registrar/actualizar/eliminar en cualquier controlador, se refresquen las 3 listas
        Runnable refrescarListas = this::refrescarListas;
        autorController.setRefrescarListas(refrescarListas);
        libroController.setRefrescarListas(refrescarListas);
        usuarioController.setRefrescarListas(refrescarListas);
        prestamoController.setRefrescarListas(refrescarListas);

        cambiarIdioma(localizacion);
    }

    //Idioma
    public final void cambiarIdioma(Locale locale) {
        localizacion = locale;
        mensajes = ResourceBundle.getBundle("ec.edu.ups.biblioteca.i18n.mensajes", localizacion);

        autorController.actualizarIdioma(mensajes);
        libroController.actualizarIdioma(mensajes);
        usuarioController.actualizarIdioma(mensajes);
        prestamoController.actualizarIdioma(mensajes, localizacion);

        refrescarListas();
    }

    //Para que al entrar a las ventanas de listas se puedan ver los datos actualizados
    private void refrescarListas() {
        libroController.listarLibros();
        usuarioController.listarUsuarios();
        prestamoController.listarPrestamos();
    }
}
