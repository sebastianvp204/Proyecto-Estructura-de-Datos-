package utilidades;

public class MapaSimple<K, V> {

    private final ArregloDinamico<K> claves;
    private final ArregloDinamico<V> valores;

    public MapaSimple() {
        this.claves = new ArregloDinamico<>();
        this.valores = new ArregloDinamico<>();
    }

    public void poner(K clave, V valor) {
        int i = indiceDe(clave);
        if (i >= 0) {
            valores.establecer(i, valor);
            return;
        }
        claves.agregar(clave);
        valores.agregar(valor);
    }

    private int indiceDe(K clave) {
        for (int i = 0; i < claves.tamanio(); i++) {
            K k = claves.obtener(i);
            if (k == null && clave == null) return i;
            if (k != null && k.equals(clave)) return i;
        }
        return -1;
    }

    public V obtener(K clave) {
        int i = indiceDe(clave);
        return i >= 0 ? valores.obtener(i) : null;
    }

    public boolean contieneClave(K clave) {
        return indiceDe(clave) >= 0;
    }

    public int tamanio() {
        return claves.tamanio();
    }

    public void limpiar() {
        claves.limpiar();
        valores.limpiar();
    }

    public ArregloDinamico<K> claves() {
        return claves;
    }
}
