package algoritmos;

import dominio.Proteina;
import dominio.ProteinaHub;
import grafo.Grafo;
import grafo.RedProteinas;
import utilidades.ArregloDinamico;


public class AnalizadorHubs {

    private final RedProteinas red;
    private double umbralHub; // número de desviaciones sobre la media (p. ej. 2.0)
    private final ArregloDinamico<ProteinaHub> hubs;

    public AnalizadorHubs(RedProteinas red) 
    {
        this.red = red;
        this.umbralHub = 1.0; // Reducido para detectar hubs en redes pequeñas
        this.hubs = new ArregloDinamico<>();
    }

    public void setUmbralHub(double desviaciones) 
    {
        this.umbralHub = Math.max(0, desviaciones);
    }

 
    public int calcularCentralidadGrado(Proteina p) 
    {
        if (p == null)
        {
             return 0;
        }
        
        Grafo<Proteina> g = red.getGrafo();
        ArregloDinamico<Proteina> vecinos = g.getVecinos(p);
        return vecinos.tamanio();
    }


    public ArregloDinamico<ProteinaHub> identificarHubs() 
    {
        hubs.limpiar();
        Grafo<Proteina> g = red.getGrafo();
        ArregloDinamico<Proteina> vertices = g.getVertices();
        int n = vertices.tamanio();
        if (n == 0) return hubs;

        ArregloDinamico<Integer> grados = new ArregloDinamico<>();
        int suma = 0;
        
        for (int i = 0; i < n; i++) 
        {
            Proteina p = vertices.obtener(i);
            int grado = calcularCentralidadGrado(p);
            grados.agregar(grado);
            suma += grado;
        }
        double promedio = (double) suma / n;

        double varianza = 0;
        for (int i = 0; i < n; i++) 
        {
            double d = grados.obtener(i) - promedio;
            varianza += d * d;
        }
        varianza = n > 1 ? varianza / (n - 1) : 0;
        double desv = Math.sqrt(varianza);
        double umbral = promedio + umbralHub * desv;

        for (int i = 0; i < n; i++) 
        {
            Proteina p = vertices.obtener(i);
            int grado = grados.obtener(i);
            if (grado >= umbral) 
            {
                ProteinaHub hub = new ProteinaHub(p, grado);
                hub.calcularImportancia((grado - promedio) / (desv > 0 ? desv : 1));
                hubs.agregar(hub);
            }
        }
        ordenarPorImportancia();
        return hubs;
    }

    public void ordenarPorImportancia() 
    {
        for (int i = 1; i < hubs.tamanio(); i++) 
        {
            ProteinaHub actual = hubs.obtener(i);
            double impActual = actual.getImportancia();
            int j = i - 1;
            
            while (j >= 0 && hubs.obtener(j).getImportancia() < impActual) 
            {
                hubs.establecer(j + 1, hubs.obtener(j));
                j--;
            }
            hubs.establecer(j + 1, actual);
        }
    }

    public ArregloDinamico<ProteinaHub> getTop10Hubs() 
    {
        ArregloDinamico<ProteinaHub> top = new ArregloDinamico<>();
        int max = Math.min(10, hubs.tamanio());
        
        for (int i = 0; i < max; i++) 
        {
            top.agregar(hubs.obtener(i));
        }
        return top;
    }

    public ArregloDinamico<ProteinaHub> getHubs() 
    {
        return hubs;
    }
}
