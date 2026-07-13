/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Usuario;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class UsuarioDAOArchivo implements UsuarioDAO {

    private String rutaArchivo;

    public UsuarioDAOArchivo() {
        File carpeta = new File("archivos");

        if (carpeta.exists() == false) {
            carpeta.mkdir();
        }

        rutaArchivo = "archivos/usuarios.ups";
        File archivo = new File(rutaArchivo);

        try {
            if (archivo.exists() == false) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error al crear el archivo de usuarios");
        }
    }

    @Override
    public void crearUsuario(Usuario usuario) {
        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "rw");
            archivo.seek(archivo.length());

            escribirCadena(archivo, usuario.getCedula());
            escribirCadena(archivo, usuario.getNombre());
            escribirCadena(archivo, usuario.getCorreo());

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de usuarios");
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo de usuarios");
        }
    }

    @Override
    public Usuario buscarUsuario(String cedula) {
        List<Usuario> usuarios = listarUsuarios();

        for (Usuario usuario : usuarios) {
            if (usuario.getCedula().equals(cedula)) {
                return usuario;
            }
        }

        return null;
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {
        List<Usuario> usuarios = listarUsuarios();

        for (Usuario usuarioExistente : usuarios) {
            if (usuarioExistente.getCedula().equals(usuario.getCedula())) {
                usuarioExistente.setNombre(usuario.getNombre());
                usuarioExistente.setCorreo(usuario.getCorreo());
                break;
            }
        }

        guardarUsuarios(usuarios);
    }

    @Override
    public boolean eliminarUsuario(String cedula) {
        List<Usuario> usuarios = listarUsuarios();
        Usuario usuarioEliminar = null;

        for (Usuario usuario : usuarios) {
            if (usuario.getCedula().equals(cedula)) {
                usuarioEliminar = usuario;
                break;
            }
        }

        if (usuarioEliminar == null) {
            return false;
        }

        usuarios.remove(usuarioEliminar);
        guardarUsuarios(usuarios);
        return true;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();

        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "r");

            while (archivo.getFilePointer() < archivo.length()) {
                String cedula = leerCadena(archivo);
                String nombre = leerCadena(archivo);
                String correo = leerCadena(archivo);

                Usuario usuario = new Usuario(cedula, nombre, correo);
                usuarios.add(usuario);
            }

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de usuarios");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de usuarios");
        }

        return usuarios;
    }

    private void guardarUsuarios(List<Usuario> usuarios) {
        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "rw");
            archivo.setLength(0);

            for (Usuario usuario : usuarios) {
                escribirCadena(archivo, usuario.getCedula());
                escribirCadena(archivo, usuario.getNombre());
                escribirCadena(archivo, usuario.getCorreo());
            }

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de usuarios");
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo de usuarios");
        }
    }

    private void escribirCadena(RandomAccessFile archivo, String cadena) throws IOException {
        archivo.writeInt(cadena.length());
        archivo.writeChars(cadena);
    }

    private String leerCadena(RandomAccessFile archivo) throws IOException {
        int longitud = archivo.readInt();
        String cadena = "";

        for (int i = 0; i < longitud; i++) {
            cadena += archivo.readChar();
        }

        return cadena;
    }
}
