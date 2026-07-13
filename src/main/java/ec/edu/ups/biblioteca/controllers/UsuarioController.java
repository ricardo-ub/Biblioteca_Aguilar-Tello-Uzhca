/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.controllers;

import ec.edu.ups.biblioteca.dao.PrestamoDAO;
import ec.edu.ups.biblioteca.dao.UsuarioDAO;
import ec.edu.ups.biblioteca.exceptions.BibliotecaExceptionPrincipal;
import ec.edu.ups.biblioteca.exceptions.BusquedaException;
import ec.edu.ups.biblioteca.exceptions.CampoObligatorioException;
import ec.edu.ups.biblioteca.exceptions.PrestamosException;
import ec.edu.ups.biblioteca.exceptions.RegistroException;
import ec.edu.ups.biblioteca.exceptions.SeleccionInvalidaException;
import ec.edu.ups.biblioteca.models.Usuario;
import ec.edu.ups.biblioteca.views.ActualizarRegistrarUsuarioView;
import ec.edu.ups.biblioteca.views.ListaUsuariosView;
import ec.edu.ups.biblioteca.views.RegistrarPrestamoView;
import ec.edu.ups.biblioteca.views.RegistrarUsuarioView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

/**
 *
 * @author DELL
 */
public class UsuarioController {

    private UsuarioDAO usuarioDAO;
    private PrestamoDAO prestamoDAO;
    private RegistrarUsuarioView registrarUsuarioView;
    private ActualizarRegistrarUsuarioView actualizarRegistroUsuarioView;
    private RegistrarPrestamoView registrarPrestamoView;
    private ListaUsuariosView listaUsuariosView;
    private ResourceBundle mensajes;
    private Runnable refrescarListas = () -> {
    };

    public UsuarioController(UsuarioDAO usuarioDAO, PrestamoDAO prestamoDAO,
            RegistrarUsuarioView registrarUsuarioView, ActualizarRegistrarUsuarioView actualizarRegistroUsuarioView,
            RegistrarPrestamoView registrarPrestamoView, ListaUsuariosView listaUsuariosView,
            ResourceBundle mensajes) {
        this.usuarioDAO = usuarioDAO;
        this.prestamoDAO = prestamoDAO;
        this.registrarUsuarioView = registrarUsuarioView;
        this.actualizarRegistroUsuarioView = actualizarRegistroUsuarioView;
        this.registrarPrestamoView = registrarPrestamoView;
        this.listaUsuariosView = listaUsuariosView;
        this.mensajes = mensajes;

        configurarEventosRegistrarUsuario();
        configurarEventosActualizarUsuario();
        configurarEventosListaUsuarios();
        cargarComboUsuarios();
    }

    public void setRefrescarListas(Runnable refrescarListas) {
        this.refrescarListas = refrescarListas;
    }

    public void actualizarIdioma(ResourceBundle mensajes) {
        this.mensajes = mensajes;
    }

    //USUARIO
    public void registrarUsuario() throws CampoObligatorioException, RegistroException {
        String cedula = registrarUsuarioView.getTxtCedula().getText().trim();
        String nombre = registrarUsuarioView.getTxtNombre().getText().trim();
        String correo = registrarUsuarioView.getTxtCorreo().getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            throw new CampoObligatorioException(mensajes.getString("mensaje.usuario.camposObligatorios"));
        }

        if (usuarioDAO.buscarUsuario(cedula) != null) {
            throw new RegistroException(mensajes.getString("mensaje.usuario.cedulaExistente"));
        }

        Usuario usuario = new Usuario(cedula, nombre, correo);
        usuarioDAO.crearUsuario(usuario);

        registrarUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.registrado"));

        registrarUsuarioView.getTxtCedula().setText("");
        registrarUsuarioView.getTxtNombre().setText("");
        registrarUsuarioView.getTxtCorreo().setText("");
    }

    private void configurarEventosRegistrarUsuario() {
        registrarUsuarioView.getBtnRegistUsu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    registrarUsuario();
                    cargarComboUsuarios();
                    refrescarListas.run();
                } catch (BibliotecaExceptionPrincipal ex) {
                    registrarUsuarioView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Intento de registro de usuario finalizado.");
                }
            }
        });
    }

    public void cargarComboUsuarios() {
        registrarPrestamoView.getCmbUsuarios().removeAllItems();

        List<Usuario> usuarios = usuarioDAO.listarUsuarios();

        for (Usuario usuario : usuarios) {
            registrarPrestamoView.getCmbUsuarios().addItem(usuario.toString());
        }
    }

    public void buscarUsuarioActualizar() throws BusquedaException {
        String cedula = actualizarRegistroUsuarioView.getTxtCedula().getText().trim();
        Usuario usuario = usuarioDAO.buscarUsuario(cedula);

        if (usuario == null) {
            throw new BusquedaException(mensajes.getString("mensaje.usuario.noEncontrado"));
        }
        actualizarRegistroUsuarioView.getTxtNombre().setText(usuario.getNombre());
        actualizarRegistroUsuarioView.getTxtCorreo().setText(usuario.getCorreo());
    }

    public void actualizarUsuario() throws CampoObligatorioException, BusquedaException {
        String cedula = actualizarRegistroUsuarioView.getTxtCedula().getText().trim();

        if (usuarioDAO.buscarUsuario(cedula) == null) {
            throw new BusquedaException(mensajes.getString("mensaje.usuario.buscarPrimero"));
        }

        String nombre = actualizarRegistroUsuarioView.getTxtNombre().getText().trim();
        String correo = actualizarRegistroUsuarioView.getTxtCorreo().getText().trim();

        if (nombre.isEmpty()) {
            throw new CampoObligatorioException(mensajes.getString("mensaje.usuario.nombreObligatorio"));
        }

        Usuario usuario = new Usuario(cedula, nombre, correo);
        usuarioDAO.actualizarUsuario(usuario);

        actualizarRegistroUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.actualizado"));
        limpiarActualizarUsuario();
    }

    public void limpiarActualizarUsuario() {
        actualizarRegistroUsuarioView.getTxtCedula().setText("");
        actualizarRegistroUsuarioView.getTxtNombre().setText("");
        actualizarRegistroUsuarioView.getTxtCorreo().setText("");
    }

    private void configurarEventosActualizarUsuario() {
        actualizarRegistroUsuarioView.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buscarUsuarioActualizar();
                } catch (BusquedaException ex) {
                    actualizarRegistroUsuarioView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Búsqueda de usuario para actualizar finalizada.");
                }
            }
        });

        actualizarRegistroUsuarioView.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actualizarUsuario();
                    cargarComboUsuarios();
                    refrescarListas.run();
                } catch (BibliotecaExceptionPrincipal ex) {
                    actualizarRegistroUsuarioView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Intento de actualización de usuario finalizado.");
                }
            }
        });
    }

    public Usuario buscarUsuarioPorNombre(String nombre) {
        for (Usuario usuario : usuarioDAO.listarUsuarios()) {
            if (usuario.getNombre().equals(nombre)) {
                return usuario;
            }
        }
        return null;
    }

    public void listarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.listarUsuarios();
        listaUsuariosView.cargarDatos(usuarios);
    }

    public void eliminarUsuarioSeleccionado() throws SeleccionInvalidaException, PrestamosException {
        int fila = listaUsuariosView.getTblUsuarios().getSelectedRow();

        if (fila == -1) {
            throw new SeleccionInvalidaException(mensajes.getString("mensaje.usuario.seleccionarTabla"));
        }

        String cedula = (String) listaUsuariosView.getTblUsuarios().getValueAt(fila, 0);
        Usuario usuario = usuarioDAO.buscarUsuario(cedula);
        int opcion = listaUsuariosView.confirmarEliminacion();

        if (opcion == JOptionPane.YES_OPTION) {
            if (usuario != null && prestamoDAO.tienePrestamoActivo(usuario)) {
                throw new PrestamosException(mensajes.getString("mensaje.usuario.noEliminarPrestamo"));
            }

            usuarioDAO.eliminarUsuario(cedula);
            listaUsuariosView.mostrarInformacion(mensajes.getString("mensaje.usuario.eliminado"));

            cargarComboUsuarios();
            refrescarListas.run();
        }
    }

    private void configurarEventosListaUsuarios() {
        listaUsuariosView.getBtnListaUsu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarUsuarios();
            }
        });

        listaUsuariosView.getBtnEliminar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    eliminarUsuarioSeleccionado();
                } catch (BibliotecaExceptionPrincipal ex) {
                    listaUsuariosView.mostrarInformacion(ex.getMessage());
                } finally {
                    System.out.println("Intento de eliminación de usuario finalizado.");
                }
            }
        });
    }
}
