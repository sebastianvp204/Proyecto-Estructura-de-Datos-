package algoritmos;

import grafo.Arista;
import grafo.Grafo;
import grafo.Vertice;
import utilidades.ArregloDinamico;
import utilidades.MapaSimple;

/**
 * Implementación del Algoritmo de Dijkstra para encontrar la ruta mínima en una red.
 * En el Proyecto BioGraph, se utiliza para identificar el camino de interacción 
 * más cort entre dos proteínas.
 * * @param <T> El tipo de dato almacenado en los vértices (ej. Proteina).
 */
public class AlgoritmoDijkstra<T> 
{

    private final Grafo<T> grafo;
    
    /** Almacena la distancia acumulada (peso) desde el origen a cada nodo. */
    private final MapaSimple<T, Double> distancias;
    
    /** Registra el nodo previo para poder reconstruir el camino al finalizar. */
    private final MapaSimple<T, T> predecesores;

    /**
     * Constructor del algoritmo.
     * @param grafo El grafo (red proteica) sobreegl cual se realizarán los cálculos.
     */
    public AlgoritmoDijkstra(Grafo<T> grafo) 
    {
        this.grafo = grafo;
        this.distancias = new MapaSimple<>();
        this.predecesores = new MapaSimple<>();
    }

    /**
     * Ejecuta el cálculo de la ruta más corta desde un nodo origen.
     * Implementa la lógica de exploración de nodos de menor costo acumulado.
     * * @param origen Nodo de inicio (ej. Proteína A).
     * @param origen
     * @param destino Nodo final (ej. Proteína B).
     */
    public void calcularRutaMasCorta(T origen, T destino) 
    {
        ArregloDinamico<T> vertices = grafo.getVertices();
        
        // Inicialización: Todas las distancias a infinito y predecesores nulos
        for (int i = 0; i < vertices.tamanio(); i++) 
        {
            T v = vertices.obtener(i);
            distancias.poner(v, Double.POSITIVE_INFINITY);
            predecesores.poner(v, null);
        }
        distancias.poner(origen, 0.0);

        MapaSimple<T, Boolean> visitado = new MapaSimple<>();
        
        for (int i = 0; i < vertices.tamanio(); i++) 
        {
            visitado.poner(vertices.obtener(i), false);
        }
        // Bucle principal: Procesa cada vértice del grafo
        for (int iter = 0; iter < vertices.tamanio(); iter++) 
        {
            // Selecciona el nodo con la distancia más corta que no ha sido visitado
            T u = obtenerMinimoNoVisitado(vertices, distancias, visitado);
            if (u == null)
            {
                break;
            }
            Double distU = distancias.obtener(u);
            // Optimización: Si llegamos al destino, detenemos el cálculo
            if (distU == null || distU == Double.POSITIVE_INFINITY)
            {
                 break;
            }
            // Optimización: Si llegamos al destino, detenemos el cálculo
            if (u.equals(destino)) break;

            visitado.poner(u, true);
            Vertice<T> verticeU = grafo.getVertice(u);
            if (verticeU == null)
            {
                 continue;
            }
            ArregloDinamico<Arista<T>> ady = verticeU.getAdyacentes();
            
            // Explorar vecinos y realizar la relajación de aristas
            for (int i = 0; i < ady.tamanio(); i++) 
            {
                Arista<T> arista = ady.obtener(i);
                Vertice<T> v = arista.getDestino();
                T datoV = v.getDato();
                relajar(u, datoV, arista.getPeso());
            }
        }
    }
    
    /**
     * Busca entre los nodos no visitados aquel que tenga la menor distancia registrada.
     */
    private T obtenerMinimoNoVisitado(ArregloDinamico<T> vertices,
                                       MapaSimple<T, Double> dist,
                                       MapaSimple<T, Boolean> visitado) {
        T mejor = null;
        double mejorDist = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < vertices.tamanio(); i++) 
        {
            T v = vertices.obtener(i);
            if (Boolean.TRUE.equals(visitado.obtener(v)))
            {
                 continue;
            }
            Double d = dist.obtener(v);
            if (d != null && d < mejorDist) 
            {
                mejorDist = d;
                mejor = v;
            }
        }
        return mejor;
    }

    /**
     * Actualiza la distancia de un nodo vecino si se encuentra un camino más corto.
     * @param u Nodo actual.
     * @param v Nodo vecino.
     * @param pesoArista Costo de la interacción entre u y v.
     */
    public void relajar(T u, T v, double pesoArista) 
    {
        Double distU = distancias.obtener(u);
        Double distV = distancias.obtener(v);
        if (distU == null || distV == null)
        {
             return;
        }
        double alternativa = distU + pesoArista;
        
        if (alternativa < distV) 
        {
            distancias.poner(v, alternativa);
            predecesores.poner(v, u);
        }
    }

        /**
     * Obtiene el costo total acumulado hasta el destino.
     */
    public double getDistancia(T destino) 
    {
        Double d = distancias.obtener(destino);
        return d != null ? d : Double.POSITIVE_INFINITY;
    }
    
        /**
     * Reconstruye el camino desde el destino al origen usando la estructura Pila.
     * @return ArregloDinamico con los nodos en orden Origen -> Destino.
     */

    public ArregloDinamico<T> reconstruirRuta(T origen, T destino) 
    {
        ArregloDinamico<T> ruta = new ArregloDinamico<>();
        T actual = destino;
        utilidades.Pila<T> pila = new utilidades.Pila<>();
        // Retrocede desde el destino usando los predecesores
        while (actual != null) 
        {
            pila.apilar(actual);
            actual = predecesores.obtener(actual);
        }
        if (pila.estaVacia())
        {
            return ruta;
        }
        // Verifica que el camino realmente conecte con el origen
        T primero = pila.verTope();
        if (!primero.equals(origen))
        {
            return ruta; 
        }
        // Invierte el orden usando la pila para retornar la ruta correcta
        while (!pila.estaVacia()) 
        {
            ruta.agregar(pila.desapilar());
        }
        return ruta;
    }
}
