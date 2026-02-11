package controlador;

import dominio.Interaccion;
import dominio.Proteina;

import java.io.File;


public class Validador {

    private String ultimoMensajeError;

    public Validador() {
        this.ultimoMensajeError = "";
    }


    public boolean validarProteina(Proteina p) {
        if (p == null) {
            ultimoMensajeError = "La proteína no puede ser nula.";
            return false;
        }
        if (!validarId(p.getId())) {
            ultimoMensajeError = "El ID de la proteína no es válido.";
            return false;
        }
        ultimoMensajeError = "";
        return true;
    }


    public boolean validarInteraccion(Interaccion i) {
        if (i == null) {
            ultimoMensajeError = "La interacción no puede ser nula.";
            return false;
        }
        if (i.getProteina1() == null || i.getProteina2() == null) {
            ultimoMensajeError = "Las proteínas de la interacción no pueden ser nulas.";
            return false;
        }
        if (!validarConfianza(i.getConfianza())) {
            ultimoMensajeError = "La confianza debe estar entre 0 y 1.";
            return false;
        }
        ultimoMensajeError = "";
        return true;
    }


    public boolean validarId(String id) {
        if (id == null || id.trim().isEmpty()) {
            ultimoMensajeError = "El ID no puede estar vacío.";
            return false;
        }
        ultimoMensajeError = "";
        return true;
    }

    public boolean validarConfianza(double conf) {
        if (conf < 0.0 || conf > 1.0) {
            ultimoMensajeError = "La confianza debe estar entre 0.0 y 1.0.";
            return false;
        }
        ultimoMensajeError = "";
        return true;
    }


    public boolean validarArchivo(String ruta) {
        if (ruta == null || ruta.trim().isEmpty()) {
            ultimoMensajeError = "La ruta del archivo no puede estar vacía.";
            return false;
        }
        File f = new File(ruta);
        if (!f.exists()) {
            ultimoMensajeError = "El archivo no existe: " + ruta;
            return false;
        }
        if (!f.canRead()) {
            ultimoMensajeError = "No se puede leer el archivo: " + ruta;
            return false;
        }
        ultimoMensajeError = "";
        return true;
    }


    public String obtenerMensajeError() {
        return ultimoMensajeError;
    }

    public boolean esFormatoValido(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) return false;
        return rutaArchivo.endsWith(".txt") || rutaArchivo.contains(".");
    }
}
