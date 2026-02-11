package controlador;

import dominio.ComplejoProteinico;
import dominio.Interaccion;
import dominio.Proteina;
import dominio.ProteinaHub;
import grafo.RedProteinas;
import utilidades.ArregloDinamico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GestorArchivos 
{
    private static final String SEPARADOR = "[\t,]";
    private final Validador validador;

    public GestorArchivos(Validador validador) 
    {
        this.validador = validador != null ? validador : new Validador();
    }

    public void cargarProteinas(String rutaArchivo, RedProteinas red) throws IOException 
    {
        if (!validador.validarArchivo(rutaArchivo) || red == null) 
        {
            throw new IOException(validador.obtenerMensajeError());
        }
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) 
        {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) 
            {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (primera && (linea.equalsIgnoreCase("ID\tNombre\tFuncion") || linea.toLowerCase().startsWith("id"))) 
                {
                    primera = false;
                    continue;
                }
                
                if (!validarFormato(linea, 3))
                {
                    continue;
                }
                String[] partes = parsearLinea(linea);
                if (partes.length >= 3) 
                {
                    String id = partes[0].trim();
                    String nombre = partes[1].trim();
                    String funcion = partes[2].trim();
                    Proteina p = new Proteina(id, nombre, funcion);
                    if (validador.validarProteina(p)) {
                        red.agregarProteina(p);
                    }
                }
            }
        }
    }

    public void cargarInteracciones(String rutaArchivo, RedProteinas red) throws IOException 
    {
        if (!validador.validarArchivo(rutaArchivo) || red == null) 
        {
            throw new IOException(validador.obtenerMensajeError());
        }
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) 
        {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) 
            {
                linea = linea.trim();
                if (linea.isEmpty())
                {
                     continue;
                }
                if (primera && (linea.toLowerCase().startsWith("proteina") || linea.toLowerCase().startsWith("id"))) 
                {
                    primera = false;
                    continue;
                }
                if (!validarFormato(linea, 3))
                {
                     continue;
                }
                String[] partes = parsearLinea(linea);
                if (partes.length >= 3) 
                {
                    String id1 = partes[0].trim();
                    String id2 = partes[1].trim();
                    double confianza;
                    try 
                    {
                        confianza = Double.parseDouble(partes[2].trim().replace(',', '.'));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    if (!validador.validarConfianza(confianza))
                    {
                        continue;
                    }
                    
                    Proteina p1 = red.getProteina(id1);
                    Proteina p2 = red.getProteina(id2);
                    if (p1 == null) p1 = new Proteina(id1, "", "");
                    if (p2 == null) p2 = new Proteina(id2, "", "");
                    red.agregarProteina(p1);
                    red.agregarProteina(p2);
                    Interaccion i = new Interaccion(p1, p2, confianza);
                    
                    if (validador.validarInteraccion(i)) 
                    {
                        red.agregarInteraccion(i);
                    }
                }
            }
        }
    }

    public void exportarRed(RedProteinas red, String rutaArchivo) throws IOException 
    {
        if (red == null || rutaArchivo == null || rutaArchivo.trim().isEmpty()) 
        {
            throw new IOException("Red o ruta de archivo no válidos.");
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) 
        {
            pw.println("# Red de proteínas");
            utilidades.MapaSimple<String, Proteina> map = red.getProteinasPorId();
            
            for (int i = 0; i < map.claves().tamanio(); i++) 
            {
                String id = map.claves().obtener(i);
                Proteina p = map.obtener(id);
                if (p != null) 
                {
                    pw.println("P\t" + p.getId() + "\t" + p.getNombre() + "\t" + p.getFuncion());
                }
            }
           
            utilidades.ArregloDinamico<Proteina> vertices = red.getGrafo().getVertices();
            
            for (int i = 0; i < vertices.tamanio(); i++) 
            {
                Proteina p1 = vertices.obtener(i);
                utilidades.ArregloDinamico<Proteina> vecinos = red.getGrafo().getVecinos(p1);
                
                for (int j = 0; j < vecinos.tamanio(); j++) 
                {
                    Proteina p2 = vecinos.obtener(j);
                    if (p1.getId().compareTo(p2.getId()) < 0) 
                    {
                        pw.println("I\t" + p1.getId() + "\t" + p2.getId() + "\t1.0");
                    }
                }
            }
        }
    }

    public void exportarComplejos(ArregloDinamico<ComplejoProteinico> complejos, String rutaArchivo) throws IOException 
    {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) 
        {
            throw new IOException("Ruta de archivo no válida.");
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) 
        {
            pw.println("# Complejos proteínicos");
            for (int c = 0; c < complejos.tamanio(); c++) 
            {
                ComplejoProteinico comp = complejos.obtener(c);
                pw.println("Complejo " + comp.getId() + " densidad=" + comp.getDensidad() + " tamaño=" + comp.getTamanio());
                for (int i = 0; i < comp.getProteinas().tamanio(); i++) 
                {
                    pw.println("  " + comp.getProteinas().obtener(i).getId());
                }
            }
        }
    }

    public void exportarHubs(ArregloDinamico<ProteinaHub> hubs, String rutaArchivo) throws IOException 
    {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) 
        {
            throw new IOException("Ruta de archivo no válida.");
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) 
        {
            pw.println("# Hubs");
            for (int i = 0; i < hubs.tamanio(); i++) 
            {
                ProteinaHub h = hubs.obtener(i);
                pw.println(h.getProteina().getId() + "\t" + h.getGradoCentralidad() + "\t" + h.getImportancia());
            }
        }
    }

    public void exportarRuta(ArregloDinamico<Proteina> ruta, String rutaArchivo) throws IOException 
    {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) 
        {
            throw new IOException("Ruta de archivo no válida.");
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) 
        {
            pw.println("# Ruta más corta");
            for (int i = 0; i < ruta.tamanio(); i++) 
            {
                pw.println(ruta.obtener(i).getId());
            }
        }
    }

    public boolean validarFormato(String linea, int minPartes) 
    {
        if (linea == null || linea.trim().isEmpty()) return false;
        String[] partes = linea.trim().split(SEPARADOR);
        return partes.length >= minPartes;
    }

    public String[] parsearLinea(String linea) 
    {
        if (linea == null) return new String[0];
        return linea.trim().split(SEPARADOR);
    }
}
