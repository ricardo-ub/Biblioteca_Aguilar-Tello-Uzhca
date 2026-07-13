/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Autor;
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
public class AutorDAOArchivo implements AutorDAO {

    private String rutaArchivo;

    public AutorDAOArchivo() {
        File carpeta = new File("archivos");

        if (carpeta.exists() == false) {
            carpeta.mkdir();
        }

        rutaArchivo = "archivos/autores.ups";
        File archivo = new File(rutaArchivo);

        try {
            if (archivo.exists() == false) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error al crear el archivo de autores");
        }
    }

    @Override
    public void crearAutor(Autor autor) {
        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "rw");
            archivo.seek(archivo.length());

            escribirCadena(archivo, autor.getNombre());
            escribirCadena(archivo, autor.getNacionalidad());
            escribirCadena(archivo, autor.getCorreo());

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de autores");
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo de autores");
        }
    }

    @Override
    public Autor buscarAutor(String nombre) {
        List<Autor> autores = listarAutores();

        for (Autor autor : autores) {
            if (autor.getNombre().equalsIgnoreCase(nombre)) {
                return autor;
            }
        }

        return null;
    }

    @Override
    public void actualizarAutor(Autor autor) {
        List<Autor> autores = listarAutores();

        for (Autor autorExistente : autores) {
            if (autorExistente.getNombre().equalsIgnoreCase(autor.getNombre())) {
                autorExistente.setNacionalidad(autor.getNacionalidad());
                autorExistente.setCorreo(autor.getCorreo());
                break;
            }
        }

        guardarAutores(autores);
    }

    @Override
    public List<Autor> listarAutores() {
        List<Autor> autores = new ArrayList<>();

        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "r");

            while (archivo.getFilePointer() < archivo.length()) {
                String nombre = leerCadena(archivo);
                String nacionalidad = leerCadena(archivo);
                String correo = leerCadena(archivo);

                Autor autor = new Autor(nombre, nacionalidad, correo);
                autores.add(autor);
            }

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de autores");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de autores");
        }

        return autores;
    }

    private void guardarAutores(List<Autor> autores) {
        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "rw");
            archivo.setLength(0);

            for (Autor autor : autores) {
                escribirCadena(archivo, autor.getNombre());
                escribirCadena(archivo, autor.getNacionalidad());
                escribirCadena(archivo, autor.getCorreo());
            }

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de autores");
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo de autores");
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
