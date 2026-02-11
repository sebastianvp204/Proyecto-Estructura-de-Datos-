package algoritmos;

import grafo.Arista;
import grafo.Grafo;
import grafo.Vertice;
import utilidades.ArregloDinamico;
import utilidades.Cola;
import utilidades.Pila;


public class AlgoritmosBusqueda<T> 
{

    private final Grafo<T> grafo;

    public AlgoritmosBusqueda(Grafo<T> grafo) 
    {
        this.grafo = grafo;
    }


    private void limpiarVisitados() 
    {
        ArregloDinamico<T> vertices = grafo.getVertices();
        
        for (int i = 0; i < vertices.tamanio(); i++) 
        {
            Vertice<T> v = grafo.getVertice(vertices.obtener(i));
            if (v != null) v.setVisitado(false);
        }
    }


    public ArregloDinamico<T> dfs(T inicio) 
    {
        limpiarVisitados();
        ArregloDinamico<T> resultado = new ArregloDinamico<>();
        Vertice<T> vInicio = grafo.getVertice(inicio);
        
        if (vInicio == null)
        {
             return resultado;
        }
        
        Pila<Vertice<T>> pila = new Pila<>();
        pila.apilar(vInicio);
        
        while (!pila.estaVacia()) 
        {
            Vertice<T> v = pila.desapilar();
            if (v.isVisitado())
            {
                 continue;
            }
            
            v.setVisitado(true);
            resultado.agregar(v.getDato());
            ArregloDinamico<Arista<T>> ady = v.getAdyacentes();
            
            for (int i = ady.tamanio() - 1; i >= 0; i--) 
            {
                Vertice<T> vecino = ady.obtener(i).getDestino();
                if (!vecino.isVisitado()) 
                {
                    pila.apilar(vecino);
                }
            }
        }
        return resultado;
    }


    public ArregloDinamico<T> bfs(T inicio) 
    {
        limpiarVisitados();
        ArregloDinamico<T> resultado = new ArregloDinamico<>();
        Vertice<T> vInicio = grafo.getVertice(inicio);
        
        if (vInicio == null)
        {
             return resultado;
        }
        
        Cola<Vertice<T>> cola = new Cola<>();
        cola.encolar(vInicio);
        vInicio.setVisitado(true);
        resultado.agregar(vInicio.getDato());
        
        while (!cola.estaVacia()) 
        {
            Vertice<T> v = cola.desencolar();
            ArregloDinamico<Arista<T>> ady = v.getAdyacentes();
            for (int i = 0; i < ady.tamanio(); i++) 
            {
                Vertice<T> vecino = ady.obtener(i).getDestino();
                if (!vecino.isVisitado()) 
                {
                    vecino.setVisitado(true);
                    resultado.agregar(vecino.getDato());
                    cola.encolar(vecino);
                }
            }
        }
        return resultado;
    }


    public ArregloDinamico<ArregloDinamico<T>> encontrarComponentes() 
    {
        limpiarVisitados();
        ArregloDinamico<ArregloDinamico<T>> componentes = new ArregloDinamico<>();
        ArregloDinamico<T> vertices = grafo.getVertices();
        for (int i = 0; i < vertices.tamanio(); i++) 
        {
            T dato = vertices.obtener(i);
            Vertice<T> v = grafo.getVertice(dato);
            if (v != null && !v.isVisitado()) 
            {
                ArregloDinamico<T> componente = bfs(dato);
                componentes.agregar(componente);
            }
        }
        return componentes;
    }


    public ArregloDinamico<T> buscarCamino(T origen, T destino) 
    {
        limpiarVisitados();
        ArregloDinamico<T> ruta = new ArregloDinamico<>();
        Vertice<T> vOrigen = grafo.getVertice(origen);
        Vertice<T> vDestino = grafo.getVertice(destino);
        if (vOrigen == null || vDestino == null) return ruta;
        
        utilidades.MapaSimple<Vertice<T>, Vertice<T>> predecesor = new utilidades.MapaSimple<>();
        Cola<Vertice<T>> cola = new Cola<>();
        cola.encolar(vOrigen);
        vOrigen.setVisitado(true);
        
        while (!cola.estaVacia()) 
        {
            Vertice<T> v = cola.desencolar();
            if (v.getDato().equals(destino)) 
            {
                Vertice<T> actual = v;
                Pila<T> pilaRuta = new Pila<>();
                
                while (actual != null) 
                {
                    pilaRuta.apilar(actual.getDato());
                    actual = predecesor.obtener(actual);
                }
                while (!pilaRuta.estaVacia()) 
                {
                    ruta.agregar(pilaRuta.desapilar());
                }
                return ruta;
            }
            
            ArregloDinamico<Arista<T>> ady = v.getAdyacentes();
            
            for (int i = 0; i < ady.tamanio(); i++) 
            {
                Vertice<T> vecino = ady.obtener(i).getDestino();
                if (!vecino.isVisitado()) 
                {
                    vecino.setVisitado(true);
                    predecesor.poner(vecino, v);
                    cola.encolar(vecino);
                }
            }
        }
        return ruta;
    }


    public boolean esConexo() 
    {
        ArregloDinamico<ArregloDinamico<T>> comp = encontrarComponentes();
        return comp.tamanio() <= 1;
    }


    public boolean detectarCiclos() 
    {
        limpiarVisitados();
        ArregloDinamico<T> vertices = grafo.getVertices();
        for (int i = 0; i < vertices.tamanio(); i++) 
        {
            Vertice<T> v = grafo.getVertice(vertices.obtener(i));
            if (v != null && !v.isVisitado() && hayCicloDesde(v, null)) 
            {
                return true;
            }
        }
        return false;
    }

    private boolean hayCicloDesde(Vertice<T> v, Vertice<T> padre) 
    {
        v.setVisitado(true);
        ArregloDinamico<Arista<T>> ady = v.getAdyacentes();
        
        for (int i = 0; i < ady.tamanio(); i++) 
        {
            Vertice<T> vecino = ady.obtener(i).getDestino();
            
            if (!vecino.isVisitado()) 
            {
                if (hayCicloDesde(vecino, v)) return true;
            } 
            else if (vecino != padre) 
            {
                return true;
            }
        }
        return false;
    }
}
