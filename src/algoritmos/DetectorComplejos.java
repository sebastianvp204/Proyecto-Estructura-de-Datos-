package algoritmos;

import dominio.ComplejoProteinico;
import dominio.Proteina;
import grafo.Grafo;
import grafo.RedProteinas;
import utilidades.ArregloDinamico;


public class DetectorComplejos 
{
    private final RedProteinas red;
    private double umbralDensidad;
    private int tamanioMinimo;
    private final ArregloDinamico<ComplejoProteinico> complejos;

    public DetectorComplejos(RedProteinas red) 
    {
        this.red = red;
        this.umbralDensidad = 0.5;
        this.tamanioMinimo = 2;
        this.complejos = new ArregloDinamico<>();
    }

    public void setUmbralDensidad(double umbral) 
    {
        this.umbralDensidad = Math.max(0.0, Math.min(1.0, umbral));
    }

    public void setTamanioMinimo(int min) 
    {
        this.tamanioMinimo = Math.max(1, min);
    }

    public ArregloDinamico<ComplejoProteinico> detectarComplejos() 
    {
        complejos.limpiar();
        AlgoritmosBusqueda<Proteina> busqueda = new AlgoritmosBusqueda<>(red.getGrafo());
        ArregloDinamico<ArregloDinamico<Proteina>> componentes = busqueda.encontrarComponentes();
        int idComplejo = 0;
        
        for (int c = 0; c < componentes.tamanio(); c++) 
        {
            ArregloDinamico<Proteina> grupo = componentes.obtener(c);
            
            if (grupo.tamanio() < tamanioMinimo)
            {
                 continue;
            }
            
            double densidad = calcularDensidad(grupo);
            
            if (densidad >= umbralDensidad && esComplejo(grupo, densidad)) 
            {
                ComplejoProteinico comp = new ComplejoProteinico("C" + (++idComplejo));
                for (int i = 0; i < grupo.tamanio(); i++) 
                {
                    comp.agregarProteina(grupo.obtener(i));
                }
                comp.setDensidad(densidad);
                complejos.agregar(comp);
            }
        }
        return complejos;
    }

    public double calcularDensidad(ArregloDinamico<Proteina> grupo) 
    {
        int n = grupo.tamanio();
        if (n < 2) return 0.0;
        Grafo<Proteina> g = red.getGrafo();
        int aristas = 0;
        
        for (int i = 0; i < n; i++) 
        {
            Proteina pi = grupo.obtener(i);
            for (int j = i + 1; j < n; j++) 
            {
                Proteina pj = grupo.obtener(j);
                if (g.existeArista(pi, pj)) aristas++;
            }
        }
        long aristasPosibles = (long) n * (n - 1) / 2;
        return aristasPosibles > 0 ? (double) aristas / aristasPosibles : 0.0;
    }

    public boolean esComplejo(ArregloDinamico<Proteina> grupo, double densidad) 
    {
        return grupo.tamanio() >= tamanioMinimo && densidad >= umbralDensidad;
    }

    public ComplejoProteinico expandirComplejo(Proteina semilla) 
    {
        ComplejoProteinico comp = new ComplejoProteinico("Expandido");
        comp.agregarProteina(semilla);
        AlgoritmosBusqueda<Proteina> busqueda = new AlgoritmosBusqueda<>(red.getGrafo());
        ArregloDinamico<Proteina> alcanzables = busqueda.bfs(semilla);
        
        for (int i = 0; i < alcanzables.tamanio(); i++) 
        {
            comp.agregarProteina(alcanzables.obtener(i));
        }
        
        double dens = calcularDensidad(comp.getProteinas());
        comp.setDensidad(dens);
        return comp;
    }

    public ArregloDinamico<ComplejoProteinico> getComplejos() 
    {
        return complejos;
    }
}
