package utilidades;

public class Cola<T> {

    private final ArregloDinamico<T> datos;

    public Cola() {
        this.datos = new ArregloDinamico<>();
    }

    public void encolar(T elemento) {
        datos.agregar(elemento);
    }

    public T desencolar() {
        if (datos.estaVacio()) {
            throw new IllegalStateException("Cola vacía");
        }
        return datos.eliminarPrimero();
    }

    public T verFrente() {
        if (datos.estaVacio()) throw new IllegalStateException("Cola vacía");
        return datos.obtener(0);
    }

    public boolean estaVacia() {
        return datos.estaVacio();
    }

    public int tamanio() {
        return datos.tamanio();
    }
}
