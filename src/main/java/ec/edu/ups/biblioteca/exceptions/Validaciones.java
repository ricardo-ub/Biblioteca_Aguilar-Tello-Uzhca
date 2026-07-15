/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.ups.biblioteca.exceptions;

import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 *
 * @author Caloo
 */
public class Validaciones {

    private static final Pattern PATRON_CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PATRON_ISBN = Pattern.compile("^[0-9-]+$");

    public Validaciones() {
    }

    public static void validarLongitud(String valor, int minimo, int maximo,
            String nombreCampo, ResourceBundle mensajes) throws LongitudInvalidaException {
        if (valor.length() < minimo || valor.length() > maximo) {
            throw new LongitudInvalidaException(
                    String.format(mensajes.getString("mensaje.validacion.longitud"), nombreCampo, minimo, maximo));
        }
    }

    /**
     * Verifica que el correo tenga un formato valido (usuario@dominio.algo).
     */
    public static void validarCorreo(String correo, ResourceBundle mensajes) throws CorreoInvalidoException {
        if (!PATRON_CORREO.matcher(correo).matches()) {
            throw new CorreoInvalidoException(mensajes.getString("mensaje.validacion.correoFormato"));
        }
    }

// validacion del isbn
    public static void validarIsbn(String isbn, ResourceBundle mensajes) throws FormatoISBNInvalidoException {
        if (!PATRON_ISBN.matcher(isbn).matches()) {
            throw new FormatoISBNInvalidoException(mensajes.getString("mensaje.validacion.isbnFormato"));
        }

        String soloDigitos = isbn.replace("-", "");
        if (soloDigitos.length() != 10 && soloDigitos.length() != 13) {
            throw new FormatoISBNInvalidoException(mensajes.getString("mensaje.validacion.isbnFormato"));
        }
    }
// validacion de la cedula

    public static void validarCedulaEcuatoriana(String cedula, ResourceBundle mensajes) throws CedulaInvalidaException {
        if (!cedula.matches("\\d{10}")) {
            throw new CedulaInvalidaException(mensajes.getString("mensaje.validacion.cedulaFormato"));
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));

        if (provincia < 1 || provincia > 24) {
            throw new CedulaInvalidaException(mensajes.getString("mensaje.validacion.cedulaInvalida"));
        }

        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;

        for (int i = 0; i < 9; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];
            if (valor >= 10) {
                valor -= 9;
            }
            suma += valor;
        }

        int digitoVerificador = Character.getNumericValue(cedula.charAt(9));
        int residuo = suma % 10;
        int resultadoEsperado = (residuo == 0) ? 0 : 10 - residuo;

        if (resultadoEsperado != digitoVerificador) {
            throw new CedulaInvalidaException(mensajes.getString("mensaje.validacion.cedulaInvalida"));
        }
    }
}
