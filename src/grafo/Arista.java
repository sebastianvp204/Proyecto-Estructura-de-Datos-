package grafo;

public class Arista<T> {

    private final Vertice<T> destino;
    private double peso;

    public Arista(Vertice<T> destino, double peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public Vertice<T> getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }
}
