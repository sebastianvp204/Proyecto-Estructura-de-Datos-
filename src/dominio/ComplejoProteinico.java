package dominio;

import utilidades.ArregloDinamico;

public class ComplejoProteinico {

    private final String id;
    private final ArregloDinamico<Proteina> proteinas;
    private double densidad;


    public ComplejoProteinico(String id) {
        this.id = id;
        this.proteinas = new ArregloDinamico<>();
        this.densidad = 0.0;
    }

    public void agregarProteina(Proteina p) {
        if (p != null && !proteinas.contiene(p)) {
            proteinas.agregar(p);
        }
    }

    public void calcularDensidad(int aristasReales) {
        int n = proteinas.tamanio();
        if (n < 2) {
            densidad = 0.0;
            return;
        }
        long aristasPosibles = (long) n * (n - 1) / 2;
        densidad = aristasPosibles > 0 ? (double) aristasReales / aristasPosibles : 0.0;
    }

    public void setDensidad(double densidad) {
        this.densidad = Math.max(0.0, Math.min(1.0, densidad));
    }

    public double getDensidad() {
        return densidad;
    }

    public int getTamanio() {
        return proteinas.tamanio();
    }

    public String getId() {
        return id;
    }

    public ArregloDinamico<Proteina> getProteinas() {
        return proteinas;
    }
}
