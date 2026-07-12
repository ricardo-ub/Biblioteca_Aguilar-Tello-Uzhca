/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.controllers;

import ec.edu.ups.biblioteca.dao.PrestamoDAO;
import ec.edu.ups.biblioteca.dao.UsuarioDAO;
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
    public void registrarUsuario() {
        String cedula = registrarUsuarioView.getTxtCedula().getText().trim();
        String nombre = registrarUsuarioView.getTxtNombre().getText().trim();
        String correo = registrarUsuarioView.getTxtCorreo().getText().trim();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            registrarUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.camposObligatorios"));
            return;
        }

        if (usuarioDAO.buscarUsuario(cedula) != null) {
            registrarUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.cedulaExistente"));
            return;
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
                registrarUsuario();
                cargarComboUsuarios();
                refrescarListas.run();
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

    public void buscarUsuarioActualizar() {
        String cedula = actualizarRegistroUsuarioView.getTxtCedula().getText().trim();
        Usuario usuario = usuarioDAO.buscarUsuario(cedula);

        if (usuario != null) {
            actualizarRegistroUsuarioView.getTxtNombre().setText(usuario.getNombre());
            actualizarRegistroUsuarioView.getTxtCorreo().setText(usuario.getCorreo());
        } else {
            actualizarRegistroUsuarioView.mostrarInformacion(mensajes.getString("mensaje.usuario.noEncontrado"));
        }
    }

    public void actualizarUsuario() {
        String cedula = actualizarRegistroUsuarioView.getTxtCedula().getText().trim();

        if (usuarioDAO.buscarUsuario(cedula) == null) {
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
                buscarUsuarioActualizar();
            }
        });

        actualizarRegistroUsuarioView.getBtnActualizar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarUsuario();
                cargarComboUsuarios();
                refrescarListas.run();
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

    public void eliminarUsuarioSeleccionado() {
        int fila = listaUsuariosView.getTblUsuarios().getSelectedRow();

        if (fila == -1) {
            listaUsuariosView.mostrarInformacion(mensajes.getString("mensaje.usuario.seleccionarTabla"));
            return;
        }

        String cedula = (String) listaUsuariosView.getTblUsuarios().getValueAt(fila, 0);
        Usuario usuario = usuarioDAO.buscarUsuario(cedula);
        int opcion = listaUsuariosView.confirmarEliminacion();

        if (opcion == JOptionPane.YES_OPTION) {
            if (usuario != null && prestamoDAO.tienePrestamoActivo(usuario)) {
                listaUsuariosView.mostrarInformacion(mensajes.getString("mensaje.usuario.noEliminarPrestamo"));
                return;
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
                eliminarUsuarioSeleccionado();
            }
        });
    }
}
