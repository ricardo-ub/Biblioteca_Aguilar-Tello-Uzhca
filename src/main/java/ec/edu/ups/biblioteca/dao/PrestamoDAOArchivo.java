/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.dao;

import ec.edu.ups.biblioteca.models.Libro;
import ec.edu.ups.biblioteca.models.Prestamo;
import ec.edu.ups.biblioteca.models.Usuario;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author User
 */
public class PrestamoDAOArchivo implements PrestamoDAO {

    private String rutaArchivo;
    private UsuarioDAO usuarioDAO;
    private LibroDAO libroDAO;

    public PrestamoDAOArchivo(UsuarioDAO usuarioDAO, LibroDAO libroDAO) {
        this.usuarioDAO = usuarioDAO;
        this.libroDAO = libroDAO;

        File carpeta = new File("archivos");

        if (carpeta.exists() == false) {
            carpeta.mkdir();
        }

        rutaArchivo = "archivos/prestamos.ups";
        File archivo = new File(rutaArchivo);

        try {
            if (archivo.exists() == false) {
                archivo.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error al crear el archivo de prestamos");
        }
    }

    @Override
    public void crearPrestamo(Prestamo prestamo) {
        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "rw");
            archivo.seek(archivo.length());

            escribirCadena(archivo, prestamo.getUsuario().getCedula());
            escribirCadena(archivo, prestamo.getLibro().getIsbn());
            archivo.writeLong(prestamo.getFechaPrestamo().getTime());
            archivo.writeLong(prestamo.getFechaDevolucion().getTime());
            archivo.writeBoolean(prestamo.isDevuelto());

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de prestamos");
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo de prestamos");
        }
    }

    @Override
    public Prestamo buscarPrestamo(String isbn) {
        List<Prestamo> prestamos = listarPrestamos();

        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLibro().getIsbn().equals(isbn) && prestamo.isDevuelto() == false) {
                return prestamo;
            }
        }

        return null;
    }

    @Override
    public void devolverLibro(String isbn) {
        List<Prestamo> prestamos = listarPrestamos();

        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLibro().getIsbn().equals(isbn) && prestamo.isDevuelto() == false) {
                prestamo.setDevuelto(true);
                break;
            }
        }

        guardarPrestamos(prestamos);
    }

    @Override
    public List<Prestamo> listarPrestamos() {
        List<Prestamo> prestamos = new ArrayList<>();

        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "r");

            while (archivo.getFilePointer() < archivo.length()) {
                String cedula = leerCadena(archivo);
                String isbn = leerCadena(archivo);
                long fechaPrestamoMilisegundos = archivo.readLong();
                long fechaDevolucionMilisegundos = archivo.readLong();
                boolean devuelto = archivo.readBoolean();

                Usuario usuario = usuarioDAO.buscarUsuario(cedula);
                Libro libro = libroDAO.buscarLibro(isbn);
                Date fechaPrestamo = new Date(fechaPrestamoMilisegundos);
                Date fechaDevolucion = new Date(fechaDevolucionMilisegundos);
                if (usuario != null && libro != null) {
                    Prestamo prestamo = new Prestamo(usuario, libro, fechaPrestamo, fechaDevolucion, devuelto);
                    prestamos.add(prestamo);
                }
            }

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de prestamos");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de prestamos");
        }

        return prestamos;
    }

    @Override
    public boolean tienePrestamoActivo(Libro libro) {
        List<Prestamo> prestamos = listarPrestamos();

        for (Prestamo prestamo : prestamos) {
            if (prestamo.getLibro().getIsbn().equals(libro.getIsbn()) && prestamo.isDevuelto() == false) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean tienePrestamoActivo(Usuario usuario) {
        List<Prestamo> prestamos = listarPrestamos();

        for (Prestamo prestamo : prestamos) {
            if (prestamo.getUsuario().getCedula().equals(usuario.getCedula()) && prestamo.isDevuelto() == false) {
                return true;
            }
        }

        return false;
    }

    private void guardarPrestamos(List<Prestamo> prestamos) {
        try {
            RandomAccessFile archivo = new RandomAccessFile(rutaArchivo, "rw");
            archivo.setLength(0);

            for (Prestamo prestamo : prestamos) {
                escribirCadena(archivo, prestamo.getUsuario().getCedula());
                escribirCadena(archivo, prestamo.getLibro().getIsbn());
                archivo.writeLong(prestamo.getFechaPrestamo().getTime());
                archivo.writeLong(prestamo.getFechaDevolucion().getTime());
                archivo.writeBoolean(prestamo.isDevuelto());
            }

            archivo.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo de prestamos");
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo de prestamos");
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

    @Override
    public void eliminarPrestamosPorLibro(String isbn) {
        List<Prestamo> prestamos = listarPrestamos();

        for (int i = prestamos.size() - 1; i >= 0; i--) {
            Prestamo prestamo = prestamos.get(i);

            if (prestamo.getLibro() != null && prestamo.getLibro().getIsbn().equals(isbn)) {
                prestamos.remove(i);
            }
        }

        guardarPrestamos(prestamos);
    }
}
