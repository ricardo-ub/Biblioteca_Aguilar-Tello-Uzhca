/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.controllers;

import ec.edu.ups.biblioteca.dao.LibroDAO;
import ec.edu.ups.biblioteca.dao.PrestamoDAO;
import ec.edu.ups.biblioteca.dao.UsuarioDAO;
import ec.edu.ups.biblioteca.models.Libro;
import ec.edu.ups.biblioteca.models.Prestamo;
import ec.edu.ups.biblioteca.models.Usuario;
import ec.edu.ups.biblioteca.views.DevolucionLibroView;
import ec.edu.ups.biblioteca.views.ListaPrestamosView;
import ec.edu.ups.biblioteca.views.RegistrarPrestamoView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

/**
 *
 * @author DELL
 */
public class PrestamoController {
    //Tarifa de multa por cada dia de atraso en la devolucion
    private static final double TARIFA_MULTA_DIARIA = 0.50;

    private LibroDAO libroDAO;
    private UsuarioDAO usuarioDAO;
    private PrestamoDAO prestamoDAO;
    private RegistrarPrestamoView registrarPrestamoView;
    private DevolucionLibroView devolucionLibroView;
    private ListaPrestamosView listaPrestamosView;
    private ResourceBundle mensajes;
    private Locale localizacion;
    private Runnable refrescarListas = () -> {
    };

    public PrestamoController(LibroDAO libroDAO, UsuarioDAO usuarioDAO, PrestamoDAO prestamoDAO,
            RegistrarPrestamoView registrarPrestamoView, DevolucionLibroView devolucionLibroView,
            ListaPrestamosView listaPrestamosView, ResourceBundle mensajes, Locale localizacion) {
        this.libroDAO = libroDAO;
        this.usuarioDAO = usuarioDAO;
        this.prestamoDAO = prestamoDAO;
        this.registrarPrestamoView = registrarPrestamoView;
        this.devolucionLibroView = devolucionLibroView;
        this.listaPrestamosView = listaPrestamosView;
        this.mensajes = mensajes;
        this.localizacion = localizacion;

        configurarEventosRegistrarPrestamo();
        configurarEventosDevolucion();
        configurarEventosListaPrestamos();
    }

    public void setRefrescarListas(Runnable refrescarListas) {
        this.refrescarListas = refrescarListas;
    }

    public void actualizarIdioma(ResourceBundle mensajes, Locale localizacion) {
        this.mensajes = mensajes;
        this.localizacion = localizacion;
    }

    private Usuario buscarUsuarioPorNombre(String nombre) {
        for (Usuario usuario : usuarioDAO.listarUsuarios()) {
            if (usuario.getNombre().equals(nombre)) {
                return usuario;
            }
        }
        return null;
    }

    private Libro buscarLibroPorTitulo(String titulo) {
        for (Libro libro : libroDAO.listarLibros()) {
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

        if (!libro.isDisponible() || prestamoDAO.buscarPrestamo(libro.getIsbn()) != null) {
            registrarPrestamoView.mostrarInformacion(mensajes.getString("mensaje.prestamo.libroPrestado"));
            return;
        }

        Prestamo prestamo = new Prestamo(usuario, libro, fechaPrestamo, fechaDevolucion, false);
        prestamoDAO.crearPrestamo(prestamo);
        //El libro deja de estar disponible mientras dure el prestamo
        libro.setDisponible(false);

        registrarPrestamoView.mostrarInformacion(mensajes.getString("mensaje.prestamo.registrado"));
    }

    private void configurarEventosRegistrarPrestamo() {
        registrarPrestamoView.getBtnRegistrarPrest().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarPrestamo();
                refrescarListas.run();
            }
        });
    }

    public void buscarPrestamoDevolucion() {
        String isbn = devolucionLibroView.getTxtISBN().getText().trim();
        Prestamo prestamo = prestamoDAO.buscarPrestamo(isbn);

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

    /**
     * Calcula los dias de atraso entre la fecha pactada de devolucion y la
     * fecha real de devolucion. Si no hay atraso retorna 0.
     */
    private long calcularDiasAtraso(Date fechaPactada, Date fechaReal) {
        if (fechaReal == null || fechaPactada == null || !fechaReal.after(fechaPactada)) {
            return 0;
        }
        long diferenciaMs = fechaReal.getTime() - fechaPactada.getTime();
        return TimeUnit.MILLISECONDS.toDays(diferenciaMs) + 1;
    }

    public void devolverLibro() {
        String isbn = devolucionLibroView.getTxtISBN().getText().trim();
        Prestamo prestamo = prestamoDAO.buscarPrestamo(isbn);
        int opcion = devolucionLibroView.confirmarEliminacion();

        if (prestamo == null) {
            devolucionLibroView.mostrarInformacion(mensajes.getString("mensaje.prestamo.noActivo"));
            return;
        }

        if (opcion == JOptionPane.YES_OPTION) {
            long diasAtraso = calcularDiasAtraso(prestamo.getFechaDevolucion(), new Date());

            prestamoDAO.devolverLibro(isbn);
            //El libro vuelve a estar disponible
            prestamo.getLibro().setDisponible(true);

            if (diasAtraso > 0) {
                double multa = diasAtraso * TARIFA_MULTA_DIARIA;
                //Formato de moneda segun la localizacion (paginas 7-16: NumberFormat)
                NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(localizacion);
                String montoFormateado = formatoMoneda.format(multa);
                //Mensaje armado con String.format, equivalente al printf visto en clase
                String mensajeMulta = String.format(mensajes.getString("mensaje.prestamo.multa"),
                        diasAtraso, montoFormateado);
                devolucionLibroView.mostrarInformacion(mensajeMulta);
            } else {
                devolucionLibroView.mostrarInformacion(mensajes.getString("mensaje.prestamo.devuelto"));
            }

            limpiarDevolucion();
        }
    }

    public void limpiarDevolucion() {
        devolucionLibroView.getTxtISBN().setText("");
        devolucionLibroView.getTxtTitulo().setText("");
        devolucionLibroView.getTxtPrestado().setText("");
    }

    private void configurarEventosDevolucion() {
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
                refrescarListas.run();
            }
        });
    }

    public void listarPrestamos() {
        List<Prestamo> prestamos = prestamoDAO.listarPrestamos();
        listaPrestamosView.cargarDatos(prestamos);
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
            prestamoDAO.devolverLibro(libro.getIsbn());
            libro.setDisponible(true);
            listaPrestamosView.mostrarInformacion(mensajes.getString("mensaje.prestamo.eliminado"));
            refrescarListas.run();
        }
    }

    private void configurarEventosListaPrestamos() {
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
}