package utilidades;

public class ArregloDinamico<T> {

    private Object[] elementos;
    private int cantidad;

    private static final int CAPACIDAD_INICIAL = 10;

    public ArregloDinamico() {
        this(CAPACIDAD_INICIAL);
    }

    @SuppressWarnings("unchecked")
    public ArregloDinamico(int capacidadInicial) {
        if (capacidadInicial < 1) {
            capacidadInicial = CAPACIDAD_INICIAL;
        }
        this.elementos = new Object[capacidadInicial];
        this.cantidad = 0;
    }

    public void agregar(T elemento) {
        if (cantidad >= elementos.length) {
            redimensionar();
        }
        elementos[cantidad] = elemento;
        cantidad++;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        return (T) elementos[indice];
    }

    public int tamanio() {
        return cantidad;
    }

    public boolean estaVacio() {
        return cantidad == 0;
    }

    private void redimensionar() {
        int nuevaCapacidad = elementos.length * 2;
        Object[] nuevo = new Object[nuevaCapacidad];
        for (int i = 0; i < cantidad; i++) {
            nuevo[i] = elementos[i];
        }
        elementos = nuevo;
    }

    public Object[] aArray() {
        Object[] copia = new Object[cantidad];
        for (int i = 0; i < cantidad; i++) {
            copia[i] = elementos[i];
        }
        return copia;
    }

    public void establecer(int indice, T elemento) {
        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        elementos[indice] = elemento;
    }


    @SuppressWarnings("unchecked")
    public T eliminarUltimo() {
        if (cantidad == 0) {
            throw new IllegalStateException("Arreglo vacío");
        }
        cantidad--;
        return (T) elementos[cantidad];
    }

    @SuppressWarnings("unchecked")
    public T eliminarPrimero() {
        if (cantidad == 0) {
            throw new IllegalStateException("Arreglo vacío");
        }
        T primero = (T) elementos[0];
        for (int i = 1; i < cantidad; i++) {
            elementos[i - 1] = elementos[i];
        }
        cantidad--;
        return primero;
    }

    public void limpiar() {
        cantidad = 0;
    }

    public boolean contiene(T elemento) {
        for (int i = 0; i < cantidad; i++) {
            if (elementos[i] == null && elemento == null) return true;
            if (elementos[i] != null && elementos[i].equals(elemento)) return true;
        }
        return false;
    }
}
