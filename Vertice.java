package grafo;

import utilidades.ArregloDinamico;


public class Vertice<T> {

    private final T dato;
    private final ArregloDinamico<Arista<T>> adyacentes;
    private boolean visitado;

    public Vertice(T dato) {
        this.dato = dato;
        this.adyacentes = new ArregloDinamico<>();
        this.visitado = false;
    }


    public T getDato() {
        return dato;
    }

    public void agregarArista(Arista<T> arista) {
        if (arista != null) {
            adyacentes.agregar(arista);
        }
    }


    public ArregloDinamico<Arista<T>> getAdyacentes() {
        return adyacentes;
    }


    public boolean isVisitado() {
        return visitado;
    }


    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }
}
