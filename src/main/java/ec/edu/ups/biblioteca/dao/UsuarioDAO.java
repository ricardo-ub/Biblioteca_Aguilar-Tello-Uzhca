/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Usuario;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface UsuarioDAO {
    void crearUsuario(Usuario usuario);

    Usuario buscarUsuario(String cedula);

    void actualizarUsuario(Usuario usuario);

    boolean eliminarUsuario(String cedula);

    List<Usuario> listarUsuarios();
}
