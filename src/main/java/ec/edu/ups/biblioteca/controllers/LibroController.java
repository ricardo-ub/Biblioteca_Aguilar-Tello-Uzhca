/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.controllers;

import ec.edu.ups.biblioteca.dao.AutorDAO;
import ec.edu.ups.biblioteca.dao.LibroDAO;
import ec.edu.ups.biblioteca.dao.PrestamoDAO;
import ec.edu.ups.biblioteca.exceptions.AnioInvalidoException;
import ec.edu.ups.biblioteca.exceptions.BibliotecaExceptionPrincipal;
import ec.edu.ups.biblioteca.exceptions.BusquedaException;
import ec.edu.ups.biblioteca.exceptions.CampoObligatorioException;
import ec.edu.ups.biblioteca.exceptions.PrestamosException;
import ec.edu.ups.biblioteca.exceptions.RegistroException;
import ec.edu.ups.biblioteca.exceptions.SeleccionInvalidaException;
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
import ec.edu.ups.biblioteca.enums.CategoriaLibro;

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
    public void registrarLibro() throws CampoObligatorioException, SeleccionInvalidaException, RegistroException, AnioInvalidoException {
        String isbn = registrarLibroView.getTxtISBN().getText().trim();
        String titulo = registrarLibroView.getTxtTitulo().getText().trim();
        String nombreAutorSeleccionado = (String) registrarLibroView.getCmbAutor().getSelectedItem();
        Autor autor = autorDAO.buscarAutor(nombreAutorSeleccionado);
        String categoriaSeleccionada = (String) registrarLibroView.getCmbCategoria().getSelectedItem();

        CategoriaLibro categoria = obtenerCategoria(categoriaSeleccionada);

        if (isbn.isEmpty() || titulo.isEmpty()) {
            throw new CampoObligatorioException(mensajes.getString("mensaje.libro.camposObligatorios"));
        }

        if (autor == null) {
            throw new SeleccionInvalidaException(mensajes.getString("mensaje.libro.seleccionarAutor"));
        }

        if (libroDAO.buscarLibro(isbn) != null) {
            throw new RegistroException(mensajes.getString("mensaje.libro.isbnExistente"));
        }

        int anio;
        try {
            anio = Integer.parseInt(registrarLibroView.getTxtAnio().getText().trim());
        } catch (NumberFormatException ex) {
            throw new AnioInvalidoException(mensajes.getString("mensaje.libro.anioInvalido"));
        }

        Libro libro = new Libro(isbn, titulo, autor, anio, categoria, true);
        libroDAO.crearLibro(libro);

        registrarLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.registrado"));
    }

    private void configurarEventosRegistrarLibro() {
        registrarLibroView.getBtnAceptar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    registrarLibro();
                    cargarComboLibros();
                    refrescarListas.run();
                } catch (BibliotecaExceptionPrincipal ex) {
                    registrarLibroView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Registro de Libro Finalizado.");
                }

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

    public void buscarLibroActualizar() throws BusquedaException {
        String isbn = actualizarRegistroLibroView.getTxtISBN().getText().trim();
        Libro libro = libroDAO.buscarLibro(isbn);

        if (libro == null) {
            throw new BusquedaException(mensajes.getString("mensaje.libro.noEncontrado"));
        }
        actualizarRegistroLibroView.getTxtTitulo().setText(libro.getTitulo());
        actualizarRegistroLibroView.getCmbAutores().setSelectedItem(libro.getAutor().getNombre());
        actualizarRegistroLibroView.getTxtAnio().setText(String.valueOf(libro.getAnio()));
        if (libro.getCategoria() != null) {
            actualizarRegistroLibroView.getCmbCategoria().setSelectedItem(mensajes.getString(libro.getCategoria().getClaveMensaje()));
        }

    }

    public void actualizarLibro() throws RegistroException, CampoObligatorioException, AnioInvalidoException {
        String isbn = actualizarRegistroLibroView.getTxtISBN().getText().trim();
        Libro libroExistente = libroDAO.buscarLibro(isbn);

        if (libroDAO.buscarLibro(isbn) == null) {
            throw new RegistroException(mensajes.getString("mensaje.libro.buscarPrimero"));
        }

        String titulo = actualizarRegistroLibroView.getTxtTitulo().getText().trim();
        String nombreAutorSeleccionado = (String) actualizarRegistroLibroView.getCmbAutores().getSelectedItem();
        Autor autor = autorDAO.buscarAutor(nombreAutorSeleccionado);
        String categoriaSeleccionada = (String) actualizarRegistroLibroView.getCmbCategoria().getSelectedItem();
        CategoriaLibro categoria = obtenerCategoria(categoriaSeleccionada);

        if (titulo.isEmpty() || autor == null) {
            throw new CampoObligatorioException(mensajes.getString("mensaje.libro.completarActualizacion"));
        }

        int anio;
        try {
            anio = Integer.parseInt(actualizarRegistroLibroView.getTxtAnio().getText().trim());
        } catch (NumberFormatException ex) {
            throw new AnioInvalidoException(mensajes.getString("mensaje.libro.anioInvalido"));
        }

        Libro libro = new Libro(isbn, titulo, autor, anio, categoria, libroExistente.isDisponible());
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
                try {
                    buscarLibroActualizar();
                } catch (BibliotecaExceptionPrincipal ex) {
                    actualizarRegistroLibroView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Búsqueda de Libro para Actualizar Finalizada");
                }

            }
        });

        actualizarRegistroLibroView.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actualizarLibro();
                    cargarComboLibros();
                    refrescarListas.run();
                } catch (BibliotecaExceptionPrincipal ex) {
                    actualizarRegistroLibroView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Actualización de libro finalizado.");
                }

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

    public void eliminarLibroSeleccionado() throws SeleccionInvalidaException, PrestamosException {
        int fila = listaLibrosView.getTblLibros().getSelectedRow();

        if (fila == -1) {
            throw new SeleccionInvalidaException(mensajes.getString("mensaje.libro.seleccionarTabla"));
        }

        String isbn = (String) listaLibrosView.getTblLibros().getValueAt(fila, 0);
        Libro libro = libroDAO.buscarLibro(isbn);
        int opcion = listaLibrosView.confirmarEliminacion();

        if (opcion == JOptionPane.YES_OPTION) {
            prestamoDAO.eliminarPrestamosPorLibro(isbn);
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
                try {
                    eliminarLibroSeleccionado();
                } catch (BibliotecaExceptionPrincipal ex) {
                    listaLibrosView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Eliminación de Libro Finalizado.");
                }
            }
        });
    }

    public void cargarComboCategorias() {
        registrarLibroView.getCmbCategoria().removeAllItems();
        actualizarRegistroLibroView.getCmbCategoria().removeAllItems();

        for (CategoriaLibro categoria : CategoriaLibro.values()) {
            String categoriaTraducida = mensajes.getString(categoria.getClaveMensaje());
            registrarLibroView.getCmbCategoria().addItem(categoriaTraducida);
            actualizarRegistroLibroView.getCmbCategoria().addItem(categoriaTraducida);
        }
    }

    private CategoriaLibro obtenerCategoria(String categoriaSeleccionada) {
        if (categoriaSeleccionada == null) {
            return null;
        }

        for (CategoriaLibro categoria : CategoriaLibro.values()) {
            String categoriaTraducida = mensajes.getString(categoria.getClaveMensaje());
            if (categoriaTraducida.equals(categoriaSeleccionada)) {
                return categoria;
            }
        }

        return null;
    }
}
