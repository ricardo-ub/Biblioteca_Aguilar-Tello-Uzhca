/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class UsuarioDAOMemoria implements UsuarioDAO {

    private List<Usuario> usuarios;

    public UsuarioDAOMemoria() {
        usuarios = new ArrayList<>();
    }

    @Override
    public void crearUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    @Override
    public Usuario buscarUsuario(String cedula) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCedula().equals(cedula)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {
        Usuario existente = buscarUsuario(usuario.getCedula());
        if (existente != null) {
            existente.setNombre(usuario.getNombre());
            existente.setCorreo(usuario.getCorreo());
        }
    }

    @Override
    public boolean eliminarUsuario(String cedula) {
        Usuario usuario = buscarUsuario(cedula);
        if (usuario == null) {
            return false;
        }
        usuarios.remove(usuario);
        return true;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarios;
    }
}
