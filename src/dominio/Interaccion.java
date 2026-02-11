package dominio;

public class Interaccion {

    private final Proteina proteina1;
    private final Proteina proteina2;
    private final double confianza;

    public Interaccion(Proteina proteina1, Proteina proteina2, double confianza) {
        this.proteina1 = proteina1;
        this.proteina2 = proteina2;
        this.confianza = Math.max(0.0, Math.min(1.0, confianza));
    }

    public Proteina getProteina1() {
        return proteina1;
    }

    public Proteina getProteina2() {
        return proteina2;
    }

    public double getConfianza() {
        return confianza;
    }
}
