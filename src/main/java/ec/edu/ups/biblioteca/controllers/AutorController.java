/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.controllers;

import ec.edu.ups.biblioteca.dao.AutorDAO;
import ec.edu.ups.biblioteca.exceptions.BibliotecaExceptionPrincipal;
import ec.edu.ups.biblioteca.exceptions.CampoObligatorioException;
import ec.edu.ups.biblioteca.exceptions.CorreoInvalidoException;
import ec.edu.ups.biblioteca.exceptions.LongitudInvalidaException;
import ec.edu.ups.biblioteca.exceptions.RegistroException;
import ec.edu.ups.biblioteca.exceptions.Validaciones;
import ec.edu.ups.biblioteca.models.Autor;
import ec.edu.ups.biblioteca.views.ActualizarRegistrarLibroView;
import ec.edu.ups.biblioteca.views.RegistrarAutorView;
import ec.edu.ups.biblioteca.views.RegistrarLibroView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author DELL
 */
public class AutorController {

    private AutorDAO autorDAO;
    private RegistrarAutorView registrarAutorView;
    private RegistrarLibroView registrarLibroView;
    private ActualizarRegistrarLibroView actualizarRegistroLibroView;
    private ResourceBundle mensajes;
    private Runnable refrescarListas = () -> {
    };

    public AutorController(AutorDAO autorDAO, RegistrarAutorView registrarAutorView,
            RegistrarLibroView registrarLibroView, ActualizarRegistrarLibroView actualizarRegistroLibroView,
            ResourceBundle mensajes) {
        this.autorDAO = autorDAO;
        this.registrarAutorView = registrarAutorView;
        this.registrarLibroView = registrarLibroView;
        this.actualizarRegistroLibroView = actualizarRegistroLibroView;
        this.mensajes = mensajes;

        configurarEventosRegistrarAutor();
        cargarComboAutores();
    }

    public void setRefrescarListas(Runnable refrescarListas) {
        this.refrescarListas = refrescarListas;
    }

    public void actualizarIdioma(ResourceBundle mensajes) {
        this.mensajes = mensajes;
    }

    public void registrarAutor() throws CampoObligatorioException, RegistroException, LongitudInvalidaException, CorreoInvalidoException {
        String nombre = registrarAutorView.getTxtNombre().getText().trim();
        String nacionalidad = registrarAutorView.getTxtNacionalidad().getText().trim();
        String correo = registrarAutorView.getTxtCorreo().getText().trim();

        if (nombre.isEmpty() || nacionalidad.isEmpty() || correo.isEmpty()) {
            throw new CampoObligatorioException(mensajes.getString("mensaje.autor.camposObligatorios"));
        }

        Validaciones.validarLongitud(nombre, 3, 60, mensajes.getString("campo.nombre"), mensajes);
        Validaciones.validarLongitud(nacionalidad, 3, 40, mensajes.getString("campo.nacionalidad"), mensajes);
        Validaciones.validarCorreo(correo, mensajes);

        if (autorDAO.buscarAutor(nombre) != null) {
            throw new RegistroException(mensajes.getString("mensaje.autor.duplicado"));
        }

        autorDAO.crearAutor(new Autor(nombre, nacionalidad, correo));
        registrarAutorView.mostrarInformacion(mensajes.getString("mensaje.autor.registrado"));

        registrarAutorView.getTxtNombre().setText("");
        registrarAutorView.getTxtNacionalidad().setText("");
        registrarAutorView.getTxtCorreo().setText("");
    }

    private void configurarEventosRegistrarAutor() {
        registrarAutorView.getBtnAceptar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    registrarAutor();
                    cargarComboAutores();

                    if (refrescarListas != null) {
                        refrescarListas.run();
                    }

                    System.out.println("Autor registrado correctamente.");

                } catch (BibliotecaExceptionPrincipal ex) {
                    registrarAutorView.mostrarInformacion(ex.getMessage());
                }

            }
        });
    }

    public void cargarComboAutores() {
        registrarLibroView.getCmbAutor().removeAllItems();
        actualizarRegistroLibroView.getCmbAutores().removeAllItems();
        List<Autor> autores = autorDAO.listarAutores();

        for (Autor autor : autores) {
            registrarLibroView.getCmbAutor().addItem(autor.toString());
            actualizarRegistroLibroView.getCmbAutores().addItem(autor.toString());
        }
    }
}
