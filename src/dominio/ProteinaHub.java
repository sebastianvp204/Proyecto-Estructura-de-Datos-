package dominio;

public class ProteinaHub {

    private final Proteina proteina;
    private final int gradoCentralidad;
    private double importancia;

    public ProteinaHub(Proteina proteina, int gradoCentralidad) {
        this.proteina = proteina;
        this.gradoCentralidad = Math.max(0, gradoCentralidad);
        this.importancia = 0.0;
    }

    public Proteina getProteina() {
        return proteina;
    }

    public int getGradoCentralidad() {
        return gradoCentralidad;
    }

    public void calcularImportancia(double valor) {
        this.importancia = valor;
    }

    public double getImportancia() {
        return importancia;
    }

    public boolean esHub() {
        return gradoCentralidad > 0;
    }
}
