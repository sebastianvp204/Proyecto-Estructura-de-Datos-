package grafo;

import dominio.Interaccion;
import dominio.Proteina;
import utilidades.MapaSimple;

public class RedProteinas {

    private final Grafo<Proteina> grafo;
    private final MapaSimple<String, Proteina> proteinasPorId;

    public RedProteinas() {
        this.grafo = new Grafo<>(false, true);
        this.proteinasPorId = new MapaSimple<>();
    }

    public void agregarProteina(Proteina p) {
        if (p == null || p.getId() == null) return;
        if (!proteinasPorId.contieneClave(p.getId())) {
            proteinasPorId.poner(p.getId(), p);
            grafo.agregarVertice(p);
        }
    }

    public void agregarInteraccion(Interaccion i) {
        if (i == null) return;
        Proteina p1 = i.getProteina1();
        Proteina p2 = i.getProteina2();
        if (p1 == null || p2 == null) return;
        agregarProteina(p1);
        agregarProteina(p2);
        double peso = 1.0 - i.getConfianza();
        grafo.agregarArista(p1, p2, peso);
    }

    public Proteina getProteina(String id) {
        return id != null ? proteinasPorId.obtener(id) : null;
    }

    public Grafo<Proteina> getGrafo() {
        return grafo;
    }

    public MapaSimple<String, Proteina> getProteinasPorId() {
        return proteinasPorId;
    }

}
