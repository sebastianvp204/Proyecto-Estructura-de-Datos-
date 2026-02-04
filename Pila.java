package utilidades;

public class Pila<T> {

    private final ArregloDinamico<T> datos;

    public Pila() {
        this.datos = new ArregloDinamico<>();
    }

    public void apilar(T elemento) {
        datos.agregar(elemento);
    }

    public T desapilar() {
        if (datos.estaVacio()) {
            throw new IllegalStateException("Pila vacía");
        }
        return datos.eliminarUltimo();
    }

    public T verTope() {
        if (datos.estaVacio()) throw new IllegalStateException("Pila vacía");
        return datos.obtener(datos.tamanio() - 1);
    }

    public boolean estaVacia() {
        return datos.estaVacio();
    }

    public int tamanio() {
        return datos.tamanio();
    }
}
