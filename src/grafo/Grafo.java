package grafo;

import utilidades.ArregloDinamico;
import utilidades.MapaSimple;

public class Grafo<T> {

    private final MapaSimple<T, Vertice<T>> vertices;
    private final boolean dirigido;
    private final boolean ponderado;

    public Grafo() {
        this(false, true);
    }

    public Grafo(boolean dirigido, boolean ponderado) {
        this.vertices = new MapaSimple<>();
        this.dirigido = dirigido;
        this.ponderado = ponderado;
    }

    public void agregarVertice(T dato) {
        if (dato == null) return;
        if (!vertices.contieneClave(dato)) {
            vertices.poner(dato, new Vertice<>(dato));
        }
    }

    public void agregarArista(T v1, T v2, double peso) {
        if (v1 == null || v2 == null) return;
        agregarVertice(v1);
        agregarVertice(v2);
        Vertice<T> vertice1 = vertices.obtener(v1);
        Vertice<T> vertice2 = vertices.obtener(v2);
        vertice1.agregarArista(new Arista<>(vertice2, peso));
        if (!dirigido) {
            vertice2.agregarArista(new Arista<>(vertice1, peso));
        }
    }

    public ArregloDinamico<T> getVecinos(T v) {
        ArregloDinamico<T> resultado = new ArregloDinamico<>();
        Vertice<T> vertice = vertices.obtener(v);
        if (vertice == null) return resultado;
        ArregloDinamico<Arista<T>> ady = vertice.getAdyacentes();
        for (int i = 0; i < ady.tamanio(); i++) {
            resultado.agregar(ady.obtener(i).getDestino().getDato());
        }
        return resultado;
    }

    public ArregloDinamico<T> getVertices() {
        ArregloDinamico<T> resultado = new ArregloDinamico<>();
        for (int i = 0; i < vertices.claves().tamanio(); i++) {
            resultado.agregar(vertices.claves().obtener(i));
        }
        return resultado;
    }

    public boolean existeArista(T v1, T v2) {
        Vertice<T> vertice1 = vertices.obtener(v1);
        if (vertice1 == null) return false;
        ArregloDinamico<Arista<T>> ady = vertice1.getAdyacentes();
        for (int i = 0; i < ady.tamanio(); i++) {
            if (ady.obtener(i).getDestino().getDato().equals(v2)) return true;
        }
        return false;
    }

    public Vertice<T> getVertice(T dato) {
        return vertices.obtener(dato);
    }

    public boolean isDirigido() {
        return dirigido;
    }

    public boolean isPonderado() {
        return ponderado;
    }

    public int cantidadVertices() {
        return vertices.tamanio();
    }
}
