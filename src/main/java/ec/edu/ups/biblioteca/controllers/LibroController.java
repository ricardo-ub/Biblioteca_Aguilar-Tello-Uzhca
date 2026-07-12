/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.controllers;

import ec.edu.ups.biblioteca.dao.AutorDAO;
import ec.edu.ups.biblioteca.dao.LibroDAO;
import ec.edu.ups.biblioteca.dao.PrestamoDAO;
import ec.edu.ups.biblioteca.models.Autor;
import ec.edu.ups.biblioteca.models.Libro;
import ec.edu.ups.biblioteca.views.ActualizarRegistrarLibroView;
import ec.edu.ups.biblioteca.views.ListaLibrosView;
import ec.edu.ups.biblioteca.views.RegistrarLibroView;
import ec.edu.ups.biblioteca.views.RegistrarPrestamoView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

/**
 *
 * @author DELL
 */
public class LibroController {
    private LibroDAO libroDAO;
    private AutorDAO autorDAO;
    private PrestamoDAO prestamoDAO;
    private RegistrarLibroView registrarLibroView;
    private ActualizarRegistrarLibroView actualizarRegistroLibroView;
    private RegistrarPrestamoView registrarPrestamoView;
    private ListaLibrosView listaLibrosView;
    private ResourceBundle mensajes;
    private Runnable refrescarListas = () -> {
    };

    public LibroController(LibroDAO libroDAO, AutorDAO autorDAO, PrestamoDAO prestamoDAO,
            RegistrarLibroView registrarLibroView, ActualizarRegistrarLibroView actualizarRegistroLibroView,
            RegistrarPrestamoView registrarPrestamoView, ListaLibrosView listaLibrosView,
            ResourceBundle mensajes) {
        this.libroDAO = libroDAO;
        this.autorDAO = autorDAO;
        this.prestamoDAO = prestamoDAO;
        this.registrarLibroView = registrarLibroView;
        this.actualizarRegistroLibroView = actualizarRegistroLibroView;
        this.registrarPrestamoView = registrarPrestamoView;
        this.listaLibrosView = listaLibrosView;
        this.mensajes = mensajes;

        configurarEventosRegistrarLibro();
        configurarEventosActualizarLibro();
        configurarEventosListaLibros();
        cargarComboCategorias();
        cargarComboLibros();
    }

    public void setRefrescarListas(Runnable refrescarListas) {
        this.refrescarListas = refrescarListas;
    }

    public void actualizarIdioma(ResourceBundle mensajes) {
        this.mensajes = mensajes;
        cargarComboCategorias();
    }

    //LIBRO
    public void registrarLibro() {
        String isbn = registrarLibroView.getTxtISBN().getText().trim();
        String titulo = registrarLibroView.getTxtTitulo().getText().trim();
        String nombreAutorSeleccionado = (String) registrarLibroView.getCmbAutor().getSelectedItem();
        Autor autor = autorDAO.buscarAutor(nombreAutorSeleccionado);
        String categoria = (String) registrarLibroView.getCmbCategoria().getSelectedItem();

        if (isbn.isEmpty() || titulo.isEmpty()) {
            registrarLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.camposObligatorios"));
            return;
        }

        if (autor == null) {
            registrarLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.seleccionarAutor"));
            return;
        }

        if (libroDAO.buscarLibro(isbn) != null) {
            registrarLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.isbnExistente"));
            return;
        }

        int anio;
        try {
            anio = Integer.parseInt(registrarLibroView.getTxtAnio().getText().trim());
        } catch (NumberFormatException ex) {
            registrarLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.anioInvalido"));
            return;
        }

        //El libro nace siempre disponible
        Libro libro = new Libro(isbn, titulo, autor, anio, categoria, true);
        libroDAO.crearLibro(libro);

        registrarLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.registrado"));
    }

    private void configurarEventosRegistrarLibro() {
        registrarLibroView.getBtnAceptar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarLibro();
                cargarComboLibros();
                refrescarListas.run();
            }
        });
    }

    public void cargarComboLibros() {
        registrarPrestamoView.getCmbLibros().removeAllItems();

        List<Libro> libros = libroDAO.listarLibros();

        for (Libro libro : libros) {
            registrarPrestamoView.getCmbLibros().addItem(libro.toString());
        }
    }

    public void buscarLibroActualizar() {
        String isbn = actualizarRegistroLibroView.getTxtISBN().getText().trim();
        Libro libro = libroDAO.buscarLibro(isbn);

        if (libro != null) {
            actualizarRegistroLibroView.getTxtTitulo().setText(libro.getTitulo());
            actualizarRegistroLibroView.getCmbAutores().setSelectedItem(libro.getAutor().getNombre());
            actualizarRegistroLibroView.getTxtAnio().setText(String.valueOf(libro.getAnio()));
            actualizarRegistroLibroView.getCmbCategoria().setSelectedItem(libro.getCategoria());
        } else {
            actualizarRegistroLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.noEncontrado"));
        }
    }

    public void actualizarLibro() {
        String isbn = actualizarRegistroLibroView.getTxtISBN().getText().trim();

        if (libroDAO.buscarLibro(isbn) == null) {
            actualizarRegistroLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.buscarPrimero"));
            return;
        }

        String titulo = actualizarRegistroLibroView.getTxtTitulo().getText().trim();
        String nombreAutorSeleccionado = (String) actualizarRegistroLibroView.getCmbAutores().getSelectedItem();
        Autor autor = autorDAO.buscarAutor(nombreAutorSeleccionado);
        String categoria = (String) actualizarRegistroLibroView.getCmbCategoria().getSelectedItem();

        if (titulo.isEmpty() || autor == null) {
            actualizarRegistroLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.completarActualizacion"));
            return;
        }

        int anio;
        try {
            anio = Integer.parseInt(actualizarRegistroLibroView.getTxtAnio().getText().trim());
        } catch (NumberFormatException ex) {
            actualizarRegistroLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.anioInvalido"));
            return;
        }

        Libro libro = new Libro(isbn, titulo, autor, anio, categoria);
        libroDAO.actualizarLibro(libro);

        actualizarRegistroLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.actualizado"));
        limpiarActualizarLibro();
    }

    public void limpiarActualizarLibro() {
        actualizarRegistroLibroView.getTxtISBN().setText("");
        actualizarRegistroLibroView.getTxtTitulo().setText("");
        actualizarRegistroLibroView.getTxtAnio().setText("");
        actualizarRegistroLibroView.getCmbCategoria().setSelectedIndex(-1);
        actualizarRegistroLibroView.getCmbAutores().setSelectedIndex(-1);
    }

    private void configurarEventosActualizarLibro() {
        actualizarRegistroLibroView.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarLibroActualizar();
            }
        });

        actualizarRegistroLibroView.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarLibro();
                cargarComboLibros();
                refrescarListas.run();
            }
        });
    }

    public Libro buscarLibroPorTitulo(String titulo) {
        for (Libro libro : libroDAO.listarLibros()) {
            if (libro.getTitulo().equals(titulo)) {
                return libro;
            }
        }
        return null;
    }

    public void listarLibros() {
        listaLibrosView.limpiarTabla();
        List<Libro> libros = libroDAO.listarLibros();
        listaLibrosView.cargarDatos(libros);
    }

    public void eliminarLibroSeleccionado() {
        int fila = listaLibrosView.getTblLibros().getSelectedRow();

        if (fila == -1) {
            listaLibrosView.mostrarInformacion(mensajes.getString("mensaje.libro.seleccionarTabla"));
            return;
        }

        String isbn = (String) listaLibrosView.getTblLibros().getValueAt(fila, 0);
        Libro libro = libroDAO.buscarLibro(isbn);
        int opcion = listaLibrosView.confirmarEliminacion();

        if (opcion == JOptionPane.YES_OPTION) {
            if (libro != null && prestamoDAO.tienePrestamoActivo(libro)) {
                listaLibrosView.mostrarInformacion(mensajes.getString("mensaje.libro.noEliminarPrestamo"));
                return;
            }

            libroDAO.eliminarLibro(isbn);
            listaLibrosView.mostrarInformacion(mensajes.getString("mensaje.libro.eliminado"));

            cargarComboLibros();
            refrescarListas.run();
        }
    }

    private void configurarEventosListaLibros() {
        listaLibrosView.getBtnListarLibros().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarLibros();
            }
        });

        listaLibrosView.getBtnEliminar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarLibroSeleccionado();
            }
        });
    }

    public void cargarComboCategorias() {
        String[] categorias = {
            mensajes.getString("categoria.novela"),
            mensajes.getString("categoria.ciencia"),
            mensajes.getString("categoria.historia"),
            mensajes.getString("categoria.tecnologia"),
            mensajes.getString("categoria.programacion"),
            mensajes.getString("categoria.matematicas"),
            mensajes.getString("categoria.infantil")
        };

        registrarLibroView.getCmbCategoria().removeAllItems();
        actualizarRegistroLibroView.getCmbCategoria().removeAllItems();

        for (String categoria : categorias) {
            registrarLibroView.getCmbCategoria().addItem(categoria);
            actualizarRegistroLibroView.getCmbCategoria().addItem(categoria);
        }
    }
}
