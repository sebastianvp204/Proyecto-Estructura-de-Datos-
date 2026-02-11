package controlador;

import dominio.ComplejoProteinico;
import dominio.Proteina;
import dominio.ProteinaHub;
import grafo.RedProteinas;
import algoritmos.AlgoritmoDijkstra;
import algoritmos.AlgoritmosBusqueda;
import algoritmos.AnalizadorHubs;
import algoritmos.DetectorComplejos;
import utilidades.ArregloDinamico;

import java.io.IOException;

public class ControladorPrincipal {

    private final RedProteinas red;
    private final DetectorComplejos detectorComplejos;
    private final AnalizadorHubs analizadorHubs;
    private final AlgoritmoDijkstra<Proteina> algoritmoDijkstra;
    private final AlgoritmosBusqueda<Proteina> algoritmosBusqueda;
    private final GestorArchivos gestorArchivos;
    private final Validador validador;

    public ControladorPrincipal() {
        this.red = new RedProteinas();
        this.validador = new Validador();
        this.gestorArchivos = new GestorArchivos(validador);
        this.algoritmosBusqueda = new AlgoritmosBusqueda<>(red.getGrafo());
        this.algoritmoDijkstra = new AlgoritmoDijkstra<>(red.getGrafo());
        this.detectorComplejos = new DetectorComplejos(red);
        this.analizadorHubs = new AnalizadorHubs(red);
    }

    public void cargarRedProteinas(String rutaProteinas, String rutaInteracciones) throws IOException {
        if (!validador.validarArchivo(rutaProteinas)) {
            throw new IOException(validador.obtenerMensajeError());
        }
        if (!validador.validarArchivo(rutaInteracciones)) {
            throw new IOException(validador.obtenerMensajeError());
        }
        gestorArchivos.cargarProteinas(rutaProteinas, red);
        gestorArchivos.cargarInteracciones(rutaInteracciones, red);
    }

    public ArregloDinamico<ComplejoProteinico> ejecutarDeteccionComplejos() {
        return detectorComplejos.detectarComplejos();
    }

    public ArregloDinamico<ProteinaHub> ejecutarIdentificacionHubs() {
        return analizadorHubs.identificarHubs();
    }

    public ArregloDinamico<Proteina> calcularRutaMasCorta(String idOrigen, String idDestino) {
        Proteina origen = red.getProteina(idOrigen);
        Proteina destino = red.getProteina(idDestino);
        if (origen == null || destino == null) {
            return new ArregloDinamico<>();
        }
        algoritmoDijkstra.calcularRutaMasCorta(origen, destino);
        return algoritmoDijkstra.reconstruirRuta(origen, destino);
    }

    public Proteina buscarProteina(String id) {
        return red.getProteina(id);
    }

    public String obtenerEstadisticas() {
        int n = red.getGrafo().cantidadVertices();
        int aristas = 0;
        ArregloDinamico<Proteina> vertices = red.getGrafo().getVertices();
        for (int i = 0; i < vertices.tamanio(); i++) {
            aristas += red.getGrafo().getVecinos(vertices.obtener(i)).tamanio();
        }
        aristas /= 2; 
        StringBuilder sb = new StringBuilder();
        sb.append("Proteínas: ").append(n).append("\n");
        sb.append("Interacciones: ").append(aristas).append("\n");
        return sb.toString();
    }

    public void exportarResultados(String rutaArchivo, String tipo,
                                   ArregloDinamico<ComplejoProteinico> complejos,
                                   ArregloDinamico<ProteinaHub> hubs,
                                   ArregloDinamico<Proteina> ruta) throws IOException {
        if (tipo == null) tipo = "";
        switch (tipo.toLowerCase()) {
            case "complejos":
                if (complejos != null) gestorArchivos.exportarComplejos(complejos, rutaArchivo);
                break;
            case "hubs":
                if (hubs != null) gestorArchivos.exportarHubs(hubs, rutaArchivo);
                break;
            case "ruta":
                if (ruta != null) gestorArchivos.exportarRuta(ruta, rutaArchivo);
                break;
            default:
                gestorArchivos.exportarRed(red, rutaArchivo);
                break;
        }
    }

    public RedProteinas getRed() {
        return red;
    }

    public DetectorComplejos getDetectorComplejos() {
        return detectorComplejos;
    }

    public AnalizadorHubs getAnalizadorHubs() {
        return analizadorHubs;
    }

    public GestorArchivos getGestorArchivos() {
        return gestorArchivos;
    }

    public Validador getValidador() {
        return validador;
    }
}
