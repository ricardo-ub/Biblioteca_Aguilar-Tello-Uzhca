/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.controllers;

import ec.edu.ups.biblioteca.dao.LibroDAO;
import ec.edu.ups.biblioteca.dao.PrestamoDAO;
import ec.edu.ups.biblioteca.dao.UsuarioDAO;
import ec.edu.ups.biblioteca.exceptions.BibliotecaExceptionPrincipal;
import ec.edu.ups.biblioteca.exceptions.BusquedaException;
import ec.edu.ups.biblioteca.exceptions.CampoObligatorioException;
import ec.edu.ups.biblioteca.exceptions.FechaInvaldiaException;
import ec.edu.ups.biblioteca.exceptions.PrestamosException;
import ec.edu.ups.biblioteca.exceptions.RegistroException;
import ec.edu.ups.biblioteca.exceptions.SeleccionInvalidaException;
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

    public void registrarPrestamo() throws SeleccionInvalidaException, FechaInvaldiaException, PrestamosException {
        String nombreUsuarioSeleccionado = (String) registrarPrestamoView.getCmbUsuarios().getSelectedItem();
        String tituloLibroSeleccionado = (String) registrarPrestamoView.getCmbLibros().getSelectedItem();

        Usuario usuario = buscarUsuarioPorNombre(nombreUsuarioSeleccionado);
        Libro libro = buscarLibroPorTitulo(tituloLibroSeleccionado);

        if (usuario == null || libro == null) {
            throw new SeleccionInvalidaException(mensajes.getString("mensaje.prestamo.seleccionInvalida"));
        }

        Date fechaPrestamo = (Date) registrarPrestamoView.getSpnFechaPrest().getValue();
        Date fechaDevolucion = (Date) registrarPrestamoView.getSpnFechaDev().getValue();

        if (fechaPrestamo == null || fechaDevolucion == null) {
            throw new FechaInvaldiaException(mensajes.getString("mensaje.prestamo.fechasObligatorias"));
        }

        if (!fechaDevolucion.after(fechaPrestamo)) {
            throw new FechaInvaldiaException(mensajes.getString("mensaje.prestamo.fechaInvalida"));
        }

        if (!libro.isDisponible() || prestamoDAO.buscarPrestamo(libro.getIsbn()) != null) {
            throw new PrestamosException(mensajes.getString("mensaje.prestamo.libroPrestado"));
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
                try {
                    registrarPrestamo();
                    refrescarListas.run();
                } catch (BibliotecaExceptionPrincipal ex) {
                    registrarPrestamoView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Registro de Préstamo finalizado.");
                }
            }
        });
    }

    public void buscarPrestamoDevolucion() throws BusquedaException {
        String isbn = devolucionLibroView.getTxtISBN().getText().trim();
        Prestamo prestamo = prestamoDAO.buscarPrestamo(isbn);

        if (prestamo != null) {
            devolucionLibroView.getTxtTitulo().setText("");
            devolucionLibroView.getTxtPrestado().setText("");
            throw new BusquedaException(mensajes.getString("mensaje.prestamo.noActivo"));
        }
        devolucionLibroView.getTxtTitulo().setText(prestamo.getLibro().getTitulo());
        devolucionLibroView.getTxtPrestado().setText(prestamo.getUsuario().getNombre());
        devolucionLibroView.getSpnFechaVen().setValue(prestamo.getFechaDevolucion());

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

    public void devolverLibro() throws BusquedaException {
        String isbn = devolucionLibroView.getTxtISBN().getText().trim();
        Prestamo prestamo = prestamoDAO.buscarPrestamo(isbn);
        int opcion = devolucionLibroView.confirmarEliminacion();

        if (prestamo == null) {
            throw new BusquedaException(mensajes.getString("mensaje.prestamo.noActivo"));
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
                try {
                    buscarPrestamoDevolucion();

                } catch (BibliotecaExceptionPrincipal ex) {
                    devolucionLibroView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Búsqueda de Préstamo Para Devolución Finalizada.");
                }
            }
        });

        devolucionLibroView.getBtnDevolver().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    devolverLibro();
                    refrescarListas.run();
                } catch (BibliotecaExceptionPrincipal ex) {
                    devolucionLibroView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Devolución de libro Finalizado.");
                }
            }
        });
    }

    public void listarPrestamos() {
        List<Prestamo> prestamos = prestamoDAO.listarPrestamos();
        listaPrestamosView.cargarDatos(prestamos);
    }

    public void eliminarPrestamoSeleccionado() throws SeleccionInvalidaException {
        int fila = listaPrestamosView.getTblPrestamos().getSelectedRow();

        if (fila == -1) {
            throw new SeleccionInvalidaException(mensajes.getString("mensaje.prestamo.seleccionarTabla"));

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
                try {
                    eliminarPrestamoSeleccionado();

                } catch (BibliotecaExceptionPrincipal ex) {
                    listaPrestamosView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Eliminación de Préstamo Finalizado.");
                }
            }
        });
    }
}
