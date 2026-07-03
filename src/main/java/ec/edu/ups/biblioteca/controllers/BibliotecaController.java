/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.controllers;

import ec.edu.ups.biblioteca.dao.BibliotecaDAO;
import ec.edu.ups.biblioteca.models.Autor;
import ec.edu.ups.biblioteca.models.Libro;
import ec.edu.ups.biblioteca.models.Prestamo;
import ec.edu.ups.biblioteca.models.Usuario;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

/**
 *
 * @author DELL
 */
public class BibliotecaController {

    private BibliotecaDAO bibliotecaDAO;
    private RegistrarAutorView registrarAutorView;
    private RegistrarLibroView registrarLibroView;
    private RegistrarUsuarioView registrarUsuarioView;
    private ActualizarRegistrarLibroView actualizarRegistroLibroView;
    private ActualizarRegistrarUsuarioView actualizarRegistroUsuarioView;
    private RegistrarPrestamoView registrarPrestamoView;
    private DevolucionLibroView devolucionLibroView;
    private ListaLibrosView listaLibrosView;
    private ListaUsuariosView listaUsuariosView;
    private ListaPrestamosView listaPrestamosView;
    private Locale localizacion;
    private ResourceBundle mensajes;

    public BibliotecaController(RegistrarAutorView registrarAutorView, RegistrarLibroView registrarLibroView, RegistrarUsuarioView registrarUsuarioView, ActualizarRegistrarLibroView actualizarRegistroLibroView, ActualizarRegistrarUsuarioView actualizarRegistroUsuarioView, RegistrarPrestamoView registrarPrestamoView, DevolucionLibroView devolucionLibroView, ListaLibrosView listaLibrosView, ListaUsuariosView listaUsuariosView, ListaPrestamosView listaPrestamosView, BibliotecaDAO bibliotecaDAO) {
        this.registrarAutorView = registrarAutorView;
        this.registrarLibroView = registrarLibroView;
        this.registrarUsuarioView = registrarUsuarioView;
        this.actualizarRegistroLibroView = actualizarRegistroLibroView;
        this.actualizarRegistroUsuarioView = actualizarRegistroUsuarioView;
        this.registrarPrestamoView = registrarPrestamoView;
        this.devolucionLibroView = devolucionLibroView;
        this.listaLibrosView = listaLibrosView;
        this.listaUsuariosView = listaUsuariosView;
        this.listaPrestamosView = listaPrestamosView;

        this.bibliotecaDAO = bibliotecaDAO;

        cambiarIdioma(new Locale("es", "EC"));

        configurarEventosRegistrarAutor();
        configurarEventosRegistrarLibro();
        configurarEventosRegistrarUsuario();

        configurarEventosActualizarLibro();
        configurarEventosActualizarUsuario();

        configurarEventosRegistrarPrestamo();
        configurarEventosDevolucion();

        configurarEventosListaLibros();
        configurarEventosListaUsuarios();
        configurarEventosListaPrestamos();
        cargarComboCategorias();
    }

    public void cambiarIdioma(Locale locale) {
        localizacion = locale;
        mensajes = ResourceBundle.getBundle("ec.edu.ups.biblioteca.i18n.mensajes", localizacion);

        cargarComboCategorias();
        listarPrestamos();
    }

    private void refrescarListas() {
        listarLibros();
        listarUsuarios();
        listarPrestamos();
    }
    //AUTOR

    public void registrarAutor() {
        String nombre = registrarAutorView.getTxtNombre().getText().trim();
        String nacionalidad = registrarAutorView.getTxtNacionalidad().getText().trim();
        String correo = registrarAutorView.getTxtCorreo().getText().trim();

        if (nombre.isEmpty()) {
            registrarAutorView.mostrarInformacion(mensajes.getString("mensaje.autor.nombreObligatorio"));
            return;
        }

        if (bibliotecaDAO.buscarAutor(nombre) != null) {
            bibliotecaDAO.actualizarAutor(new Autor(nombre, nacionalidad, correo));
            registrarAutorView.mostrarInformacion(mensajes.getString("mensaje.autor.actualizado"));
        } else {
            bibliotecaDAO.crearAutor(new Autor(nombre, nacionalidad, correo));
            registrarAutorView.mostrarInformacion(mensajes.getString("mensaje.autor.registrado"));
        }

        registrarAutorView.getTxtNombre().setText("");
        registrarAutorView.getTxtNacionalidad().setText("");
        registrarAutorView.getTxtCorreo().setText("");
    }

    public void configurarEventosRegistrarAutor() {
        registrarAutorView.getBtnAceptar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarAutor();
                cargarComboAutores();
                refrescarListas();
            }
        });
    }

    public void cargarComboAutores() {
        registrarLibroView.getCmbAutor().removeAllItems();
        actualizarRegistroLibroView.getCmbAutores().removeAllItems();
        List<Autor> autores = bibliotecaDAO.listarAutores();

        for (Autor autor : autores) {
            registrarLibroView.getCmbAutor().addItem(autor.toString());
            actualizarRegistroLibroView.getCmbAutores().addItem(autor.toString());
        }
    }

    //LIBRO
    public void registrarLibro() {
        String isbn = registrarLibroView.getTxtISBN().getText().trim();
        String titulo = registrarLibroView.getTxtTitulo().getText().trim();
        String nombreAutorSeleccionado = (String) registrarLibroView.getCmbAutor().getSelectedItem();
        Autor autor = bibliotecaDAO.buscarAutor(nombreAutorSeleccionado);
        String categoria = (String) registrarLibroView.getCmbCategoria().getSelectedItem();

        if (isbn.isEmpty() || titulo.isEmpty()) {
            registrarLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.camposObligatorios"));
            return;
        }

        if (autor == null) {
            registrarLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.seleccionarAutor"));
            return;
        }

        if (bibliotecaDAO.buscarLibro(isbn) != null) {
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

        Libro libro = new Libro(isbn, titulo, autor, anio, categoria);
        bibliotecaDAO.crearLibro(libro);

        registrarLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.registrado"));
    }

    public void configurarEventosRegistrarLibro() {
        registrarLibroView.getBtnAceptar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarLibro();
                cargarComboLibros();
                refrescarListas();
            }
        });
    }

    public void cargarComboLibros() {
        registrarPrestamoView.getCmbLibros().removeAllItems();

        List<Libro> libros = bibliotecaDAO.listarLibros();

        for (Libro libro : libros) {
            registrarPrestamoView.getCmbLibros().addItem(libro.toString());
        }
    }

    //USUARIO
    public void registrarUsuario() {
        String cedula = registrarUsuarioView.getTxtCedula().getText().trim();
        String nombre = registrarUsuarioView.getTxtNombre().getText().trim();
        String correo = registrarUsuarioView.getTxtCorreo().getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            registrarUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.camposObligatorios"));
            return;
        }

        if (bibliotecaDAO.buscarUsuario(cedula) != null) {
            registrarUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.cedulaExistente"));
            return;
        }

        Usuario usuario = new Usuario(cedula, nombre, correo);
        bibliotecaDAO.crearUsuario(usuario);

        registrarUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.registrado"));

        registrarUsuarioView.getTxtCedula().setText("");
        registrarUsuarioView.getTxtNombre().setText("");
        registrarUsuarioView.getTxtCorreo().setText("");
    }

    public void configurarEventosRegistrarUsuario() {
        registrarUsuarioView.getBtnRegistUsu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
                cargarComboUsuarios();
                refrescarListas();
            }
        });
    }

    public void cargarComboUsuarios() {
        registrarPrestamoView.getCmbUsuarios().removeAllItems();

        List<Usuario> usuarios = bibliotecaDAO.listarUsuarios();

        for (Usuario usuario : usuarios) {
            registrarPrestamoView.getCmbUsuarios().addItem(usuario.toString());
        }
    }

    public void buscarLibroActualizar() {
        String isbn = actualizarRegistroLibroView.getTxtISBN().getText().trim();
        Libro libro = bibliotecaDAO.buscarLibro(isbn);

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

        if (bibliotecaDAO.buscarLibro(isbn) == null) {
            actualizarRegistroLibroView.mostrarInformacion(mensajes.getString("mensaje.libro.buscarPrimero"));
            return;
        }

        String titulo = actualizarRegistroLibroView.getTxtTitulo().getText().trim();
        String nombreAutorSeleccionado = (String) actualizarRegistroLibroView.getCmbAutores().getSelectedItem();
        Autor autor = bibliotecaDAO.buscarAutor(nombreAutorSeleccionado);
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
        bibliotecaDAO.actualizarLibro(libro);

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

    public void configurarEventosActualizarLibro() {
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
                refrescarListas();
            }
        });
    }

    public void buscarUsuarioActualizar() {
        String cedula = actualizarRegistroUsuarioView.getTxtCedula().getText().trim();
        Usuario usuario = bibliotecaDAO.buscarUsuario(cedula);

        if (usuario != null) {
            actualizarRegistroUsuarioView.getTxtNombre().setText(usuario.getNombre());
            actualizarRegistroUsuarioView.getTxtCorreo().setText(usuario.getCorreo());
        } else {
            actualizarRegistroUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.noEncontrado"));
        }
    }

    public void actualizarUsuario() {
        String cedula = actualizarRegistroUsuarioView.getTxtCedula().getText().trim();

        if (bibliotecaDAO.buscarUsuario(cedula) == null) {
            actualizarRegistroUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.buscarPrimero"));
            return;
        }

        String nombre = actualizarRegistroUsuarioView.getTxtNombre().getText().trim();
        String correo = actualizarRegistroUsuarioView.getTxtCorreo().getText().trim();

        if (nombre.isEmpty()) {
            actualizarRegistroUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.nombreObligatorio"));
            return;
        }

        Usuario usuario = new Usuario(cedula, nombre, correo);
        bibliotecaDAO.actualizarUsuario(usuario);

        actualizarRegistroUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.actualizado"));
        limpiarActualizarUsuario();
    }

    public void limpiarActualizarUsuario() {
        actualizarRegistroUsuarioView.getTxtCedula().setText("");
        actualizarRegistroUsuarioView.getTxtNombre().setText("");
        actualizarRegistroUsuarioView.getTxtCorreo().setText("");
    }

    public void configurarEventosActualizarUsuario() {
        actualizarRegistroUsuarioView.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarUsuarioActualizar();
            }
        });

        actualizarRegistroUsuarioView.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarUsuario();
                cargarComboUsuarios();
                refrescarListas();
            }
        });
    }

    private Usuario buscarUsuarioPorNombre(String nombre) {
        for (Usuario usuario : bibliotecaDAO.listarUsuarios()) {
            if (usuario.getNombre().equals(nombre)) {
                return usuario;
            }
        }
        return null;
    }

    private Libro buscarLibroPorTitulo(String titulo) {
        for (Libro libro : bibliotecaDAO.listarLibros()) {
            if (libro.getTitulo().equals(titulo)) {
                return libro;
            }
        }
        return null;
    }

    public void registrarPrestamo() {
        String nombreUsuarioSeleccionado = (String) registrarPrestamoView.getCmbUsuarios().getSelectedItem();
        String tituloLibroSeleccionado = (String) registrarPrestamoView.getCmbLibros().getSelectedItem();

        Usuario usuario = buscarUsuarioPorNombre(nombreUsuarioSeleccionado);
        Libro libro = buscarLibroPorTitulo(tituloLibroSeleccionado);

        if (usuario == null || libro == null) {
            registrarPrestamoView.mostrarInformacion(mensajes.getString("mensaje.prestamo.seleccionInvalida"));
            return;
        }

        Date fechaPrestamo = (Date) registrarPrestamoView.getSpnFechaPrest().getValue();
        Date fechaDevolucion = (Date) registrarPrestamoView.getSpnFechaDev().getValue();

        if (fechaPrestamo == null || fechaDevolucion == null) {
            registrarPrestamoView.mostrarInformacion(mensajes.getString("mensaje.prestamo.fechasObligatorias"));
            return;
        }

        if (!fechaDevolucion.after(fechaPrestamo)) {
            registrarPrestamoView.mostrarInformacion(mensajes.getString("mensaje.prestamo.fechaInvalida"));
            return;
        }

        if (bibliotecaDAO.buscarPrestamo(libro.getIsbn()) != null) {
            registrarPrestamoView.mostrarInformacion(mensajes.getString("mensaje.prestamo.libroPrestado"));
            return;
        }

        Prestamo prestamo = new Prestamo(usuario, libro, fechaPrestamo, fechaDevolucion, false);
        bibliotecaDAO.crearPrestamo(prestamo);

        registrarPrestamoView.mostrarInformacion(mensajes.getString("mensaje.prestamo.registrado"));
    }

    public void configurarEventosRegistrarPrestamo() {
        registrarPrestamoView.getBtnRegistrarPrest().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarPrestamo();
                refrescarListas();
            }
        });
    }

    public void buscarPrestamoDevolucion() {
        String isbn = devolucionLibroView.getTxtISBN().getText().trim();
        Prestamo prestamo = bibliotecaDAO.buscarPrestamo(isbn);

        if (prestamo != null) {
            devolucionLibroView.getTxtTitulo().setText(prestamo.getLibro().getTitulo());
            devolucionLibroView.getTxtPrestado().setText(prestamo.getUsuario().getNombre());
            devolucionLibroView.getSpnFechaVen().setValue(prestamo.getFechaDevolucion());
        } else {
            devolucionLibroView.getTxtTitulo().setText("");
            devolucionLibroView.getTxtPrestado().setText("");
            devolucionLibroView.mostrarInformacion(mensajes.getString("mensaje.prestamo.noActivo"));
        }
    }

    public void devolverLibro() {
        String isbn = devolucionLibroView.getTxtISBN().getText().trim();
        Prestamo prestamo = bibliotecaDAO.buscarPrestamo(isbn);
        int opcion = devolucionLibroView.confirmarEliminacion();

        if (prestamo == null) {
            devolucionLibroView.mostrarInformacion(mensajes.getString("mensaje.prestamo.noActivo"));
            return;
        }
        if (opcion == JOptionPane.YES_OPTION) {
            bibliotecaDAO.devolverLibro(isbn);
            devolucionLibroView.mostrarInformacion(mensajes.getString("mensaje.prestamo.devuelto"));
            limpiarDevolucion();
        }
    }

    public void limpiarDevolucion() {
        devolucionLibroView.getTxtISBN().setText("");
        devolucionLibroView.getTxtTitulo().setText("");
        devolucionLibroView.getTxtPrestado().setText("");
    }

    public void configurarEventosDevolucion() {
        devolucionLibroView.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarPrestamoDevolucion();
            }
        });

        devolucionLibroView.getBtnDevolver().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                devolverLibro();
                refrescarListas();
            }
        });
    }

    public void eliminarLibroSeleccionado() {
        int fila = listaLibrosView.getTblLibros().getSelectedRow();

        if (fila == -1) {
            listaLibrosView.mostrarInformacion(mensajes.getString("mensaje.libro.seleccionarTabla"));
            return;
        }

        String isbn = (String) listaLibrosView.getTblLibros().getValueAt(fila, 0);
        int opcion = listaLibrosView.confirmarEliminacion();

        if (opcion == JOptionPane.YES_OPTION) {
            boolean eliminado = bibliotecaDAO.eliminarLibro(isbn);

            if (eliminado) {
                listaLibrosView.mostrarInformacion(mensajes.getString("mensaje.libro.eliminado"));
            } else {
                listaLibrosView.mostrarInformacion(mensajes.getString("mensaje.libro.noEliminarPrestamo"));
            }

            cargarComboLibros();
            refrescarListas();
        }
    }

    public void eliminarUsuarioSeleccionado() {
        int fila = listaUsuariosView.getTblUsuarios().getSelectedRow();

        if (fila == -1) {
            listaUsuariosView.mostrarInformacion(mensajes.getString("mensaje.usuario.seleccionarTabla"));
            return;
        }

        String cedula = (String) listaUsuariosView.getTblUsuarios().getValueAt(fila, 0);
        int opcion = listaUsuariosView.confirmarEliminacion();

        if (opcion == JOptionPane.YES_OPTION) {
            boolean eliminado = bibliotecaDAO.eliminarUsuario(cedula);

            if (eliminado) {
                listaUsuariosView.mostrarInformacion(mensajes.getString("mensaje.usuario.eliminado"));
            } else {
                listaUsuariosView.mostrarInformacion(mensajes.getString("mensaje.usuario.noEliminarPrestamo"));
            }

            cargarComboUsuarios();
            refrescarListas();
        }
    }

    public void listarLibros() {
        listaLibrosView.limpiarTabla();
        List<Libro> libros = bibliotecaDAO.listarLibros();
        listaLibrosView.cargarDatos(libros);
    }

    public void configurarEventosListaLibros() {

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

    public void listarUsuarios() {
        List<Usuario> usuarios = bibliotecaDAO.listarUsuarios();
        listaUsuariosView.cargarDatos(usuarios);
    }

    public void configurarEventosListaUsuarios() {

        listaUsuariosView.getBtnListaUsu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarUsuarios();
            }
        });

        listaUsuariosView.getBtnEliminar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarUsuarioSeleccionado();
            }
        });
    }

    public void listarPrestamos() {
        List<Prestamo> prestamos = bibliotecaDAO.listarPrestamos();
        listaPrestamosView.cargarDatos(prestamos);
    }

    public void configurarEventosListaPrestamos() {
        listaPrestamosView.getBtnListaPrest().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarPrestamos();
            }
        });

        listaPrestamosView.getBtnEliminar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarPrestamoSeleccionado();
            }
        });
    }

    public void eliminarPrestamoSeleccionado() {
        int fila = listaPrestamosView.getTblPrestamos().getSelectedRow();

        if (fila == -1) {
            listaPrestamosView.mostrarInformacion(mensajes.getString("mensaje.prestamo.seleccionarTabla"));
            return;
        }

        String titulo = String.valueOf(listaPrestamosView.getTblPrestamos().getValueAt(fila, 0));
        Libro libro = buscarLibroPorTitulo(titulo);

        if (libro == null) {
            return;
        }

        int opcion = listaPrestamosView.confirmarEliminacion();

        if (opcion == JOptionPane.YES_OPTION) {
            bibliotecaDAO.devolverLibro(libro.getIsbn());
            listaPrestamosView.mostrarInformacion(mensajes.getString("mensaje.prestamo.eliminado"));
            refrescarListas();
        }
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
