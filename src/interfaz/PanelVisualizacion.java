package interfaz;

import dominio.ComplejoProteinico;
import dominio.Proteina;
import dominio.ProteinaHub;
import grafo.RedProteinas;
import utilidades.ArregloDinamico;
import utilidades.MapaSimple;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;


public class PanelVisualizacion extends JPanel {

    private RedProteinas red;
    private final MapaSimple<Proteina, int[]> posicionesNodos;
    private double zoom;
    private static final int RADIO_NODO = 18;
    private static final int MARGEN = 60;
    private ArregloDinamico<ComplejoProteinico> complejosResaltados;
    private ArregloDinamico<ProteinaHub> hubsResaltados;
    private ArregloDinamico<Proteina> rutaResaltada;
    private Proteina nodoSeleccionado;
    private ListenerSeleccionNodo listenerSeleccion;

    public interface ListenerSeleccionNodo {
        void nodoSeleccionado(Proteina proteina);
    }

    public PanelVisualizacion() {
        this.red = null;
        this.posicionesNodos = new MapaSimple<>();
        this.zoom = 1.0;
        this.complejosResaltados = new ArregloDinamico<>();
        this.hubsResaltados = new ArregloDinamico<>();
        this.rutaResaltada = new ArregloDinamico<>();
        this.nodoSeleccionado = null;
        setBackground(new Color(245, 248, 250));
        setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Proteina p = obtenerNodoEn(x, y);
                if (p != null) {
                    nodoSeleccionado = p;
                    if (listenerSeleccion != null) {
                        listenerSeleccion.nodoSeleccionado(p);
                    }
                    repaint();
                }
            }
        });
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (red != null && getWidth() > 100 && getHeight() > 100) {
                    aplicarLayoutFuerza();
                    repaint();
                }
            }
        });
    }

    public void setListenerSeleccion(ListenerSeleccionNodo listener) {
        this.listenerSeleccion = listener;
    }

    public void setRed(RedProteinas red) {
        this.red = red;
        posicionesNodos.limpiar();
        complejosResaltados = new ArregloDinamico<>();
        hubsResaltados = new ArregloDinamico<>();
        rutaResaltada = new ArregloDinamico<>();
        nodoSeleccionado = null;
        aplicarLayoutFuerza();
        repaint();
    }

    public RedProteinas getRed() {
        return red;
    }

    public void setZoom(double zoom) {
        this.zoom = Math.max(0.2, Math.min(3.0, zoom));
        repaint();
    }

    public double getZoom() {
        return zoom;
    }

    public Proteina getNodoSeleccionado() {
        return nodoSeleccionado;
    }

    public void setNodoSeleccionado(Proteina p) {
        this.nodoSeleccionado = p;
        repaint();
    }

    public void aplicarLayoutFuerza() {
        if (red == null) return;
        ArregloDinamico<Proteina> vertices = red.getGrafo().getVertices();
        int n = vertices.tamanio();
        if (n == 0) return;
        
        int ancho = Math.max(getWidth() - 2 * MARGEN, 300);
        int alto = Math.max(getHeight() - 2 * MARGEN, 200);
        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;
        if (centroX < MARGEN) centroX = ancho / 2 + MARGEN;
        if (centroY < MARGEN) centroY = alto / 2 + MARGEN;
        
        double radioInicial = Math.min(ancho, alto) * 0.35;
        for (int i = 0; i < n; i++) {
            Proteina p = vertices.obtener(i);
            double ang = 2 * Math.PI * i / n;
            int x = (int) (centroX + radioInicial * Math.cos(ang));
            int y = (int) (centroY + radioInicial * Math.sin(ang));
            posicionesNodos.poner(p, new int[]{ x, y });
        }
        
        int iteraciones = 80;
        double k = Math.sqrt((double) ancho * alto / (n + 1)) * 0.8;
        double temp = ancho / 8.0;
        
        for (int it = 0; it < iteraciones; it++) {
            MapaSimple<Proteina, double[]> fuerzas = new MapaSimple<>();
            for (int i = 0; i < n; i++) {
                fuerzas.poner(vertices.obtener(i), new double[]{ 0, 0 });
            }
      
            for (int i = 0; i < n; i++) {
                Proteina pi = vertices.obtener(i);
                int[] posi = posicionesNodos.obtener(pi);
                if (posi == null) continue;
                for (int j = i + 1; j < n; j++) {
                    Proteina pj = vertices.obtener(j);
                    int[] posj = posicionesNodos.obtener(pj);
                    if (posj == null) continue;
                    double dx = posi[0] - posj[0];
                    double dy = posi[1] - posj[1];
                    double d = Math.sqrt(dx * dx + dy * dy) + 0.01;
                    double f = k * k / d;
                    double[] fi = fuerzas.obtener(pi);
                    double[] fj = fuerzas.obtener(pj);
                    fi[0] += f * dx / d;
                    fi[1] += f * dy / d;
                    fj[0] -= f * dx / d;
                    fj[1] -= f * dy / d;
                }
            }
           
            for (int i = 0; i < n; i++) {
                Proteina pi = vertices.obtener(i);
                int[] posi = posicionesNodos.obtener(pi);
                if (posi == null) continue;
                ArregloDinamico<Proteina> vecinos = red.getGrafo().getVecinos(pi);
                for (int j = 0; j < vecinos.tamanio(); j++) {
                    Proteina pj = vecinos.obtener(j);
                    int[] posj = posicionesNodos.obtener(pj);
                    if (posj == null) continue;
                    double dx = posj[0] - posi[0];
                    double dy = posj[1] - posi[1];
                    double d = Math.sqrt(dx * dx + dy * dy) + 0.01;
                    double f = d * d / k;
                    double[] fi = fuerzas.obtener(pi);
                    fi[0] += f * dx / d * 0.5;
                    fi[1] += f * dy / d * 0.5;
                }
            }
           
            for (int i = 0; i < n; i++) {
                Proteina p = vertices.obtener(i);
                int[] pos = posicionesNodos.obtener(p);
                double[] f = fuerzas.obtener(p);
                if (pos == null || f == null) continue;
                double despl = Math.sqrt(f[0]*f[0] + f[1]*f[1]);
                if (despl > 0.01) {
                    double limitedDespl = Math.min(despl, temp);
                    pos[0] += (int)(f[0] / despl * limitedDespl);
                    pos[1] += (int)(f[1] / despl * limitedDespl);
                    pos[0] = Math.max(MARGEN, Math.min(getWidth() - MARGEN, pos[0]));
                    pos[1] = Math.max(MARGEN, Math.min(getHeight() - MARGEN, pos[1]));
                    posicionesNodos.poner(p, pos);
                }
            }
            temp *= 0.95; 
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.scale(zoom, zoom);
        
        if (red == null || red.getGrafo().cantidadVertices() == 0) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("SansSerif", Font.ITALIC, 14));
            String msg = "Cargue datos para visualizar la red";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);
            return;
        }
        
        dibujarGrafo(g2d);
        dibujarLeyenda(g2d);
    }

    private void dibujarGrafo(Graphics2D g2d) {
        ArregloDinamico<Proteina> vertices = red.getGrafo().getVertices();
        
        for (int i = 0; i < vertices.tamanio(); i++) {
            Proteina p1 = vertices.obtener(i);
            int[] pos1 = posicionesNodos.obtener(p1);
            if (pos1 == null) continue;
            ArregloDinamico<Proteina> vecinos = red.getGrafo().getVecinos(p1);
            for (int j = 0; j < vecinos.tamanio(); j++) {
                Proteina p2 = vecinos.obtener(j);
                if (p1.getId().compareTo(p2.getId()) > 0) continue;
                int[] pos2 = posicionesNodos.obtener(p2);
                if (pos2 == null) continue;
                boolean enRuta = estaEnRuta(p1) && estaEnRuta(p2) && sonAdyacentesEnRuta(p1, p2);
                if (enRuta) {
                    g2d.setColor(new Color(255, 140, 0));
                    g2d.setStroke(new BasicStroke(4f));
                } else {
                    g2d.setColor(new Color(180, 180, 180, 150));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.draw(new Line2D.Double(pos1[0], pos1[1], pos2[0], pos2[1]));
            }
        }
        
        g2d.setStroke(new BasicStroke(2f));
        
        for (int i = 0; i < vertices.tamanio(); i++) {
            Proteina p = vertices.obtener(i);
            int[] pos = posicionesNodos.obtener(p);
            if (pos != null) {
                dibujarNodo(p, g2d, pos[0], pos[1]);
            }
        }
    }

    private void dibujarNodo(Proteina p, Graphics2D g2d, int x, int y) {
        Color colorFondo;
        Color colorBorde = Color.DARK_GRAY;
        
        boolean seleccionado = p.equals(nodoSeleccionado);
        boolean hub = esHub(p);
        boolean enComplejo = estaEnComplejo(p);
        boolean enRuta = estaEnRuta(p);
        
        if (seleccionado) {
            colorFondo = new Color(50, 205, 50); 
            colorBorde = new Color(0, 100, 0);
        } else if (enRuta) {
            colorFondo = new Color(255, 165, 0); 
            colorBorde = new Color(200, 100, 0);
        } else if (hub) {
            colorFondo = new Color(220, 20, 60); 
            colorBorde = new Color(139, 0, 0);
        } else if (enComplejo) {
            colorFondo = new Color(186, 85, 211); 
            colorBorde = new Color(128, 0, 128);
        } else {
            colorFondo = new Color(70, 130, 180); 
            colorBorde = new Color(25, 25, 112);
        }
        
        int radio = seleccionado ? RADIO_NODO + 4 : RADIO_NODO;
        
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fill(new Ellipse2D.Double(x - radio/2 + 2, y - radio/2 + 2, radio, radio));
        
        g2d.setColor(colorFondo);
        g2d.fill(new Ellipse2D.Double(x - radio/2, y - radio/2, radio, radio));
        g2d.setColor(colorBorde);
        g2d.setStroke(new BasicStroke(seleccionado ? 3f : 2f));
        g2d.draw(new Ellipse2D.Double(x - radio/2, y - radio/2, radio, radio));
        
        g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        String id = p.getId();
        int textX = x - fm.stringWidth(id) / 2;
        int textY = y + radio/2 + fm.getHeight();
        
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRoundRect(textX - 2, textY - fm.getAscent(), fm.stringWidth(id) + 4, fm.getHeight(), 4, 4);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(id, textX, textY);
    }
    
    private void dibujarLeyenda(Graphics2D g2d) {
        int x = 10;
        int y = 20;
        int size = 12;
        int spacing = 20;
        
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        g2d.setColor(new Color(70, 130, 180));
        g2d.fillOval(x, y, size, size);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Normal", x + size + 5, y + size - 2);
        y += spacing;
        
        g2d.setColor(new Color(50, 205, 50));
        g2d.fillOval(x, y, size, size);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Seleccionado", x + size + 5, y + size - 2);
        y += spacing;
        
        g2d.setColor(new Color(220, 20, 60));
        g2d.fillOval(x, y, size, size);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Hub", x + size + 5, y + size - 2);
        y += spacing;
        
        g2d.setColor(new Color(186, 85, 211));
        g2d.fillOval(x, y, size, size);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Complejo", x + size + 5, y + size - 2);
        y += spacing;
        
        g2d.setColor(new Color(255, 165, 0));
        g2d.fillOval(x, y, size, size);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Ruta", x + size + 5, y + size - 2);
    }

    private boolean estaEnRuta(Proteina p) {
        for (int i = 0; i < rutaResaltada.tamanio(); i++) {
            if (rutaResaltada.obtener(i).equals(p)) return true;
        }
        return false;
    }

    private boolean sonAdyacentesEnRuta(Proteina p1, Proteina p2) {
        for (int i = 0; i < rutaResaltada.tamanio() - 1; i++) {
            if (rutaResaltada.obtener(i).equals(p1) && rutaResaltada.obtener(i + 1).equals(p2)) return true;
            if (rutaResaltada.obtener(i).equals(p2) && rutaResaltada.obtener(i + 1).equals(p1)) return true;
        }
        return false;
    }

    private boolean esHub(Proteina p) {
        for (int i = 0; i < hubsResaltados.tamanio(); i++) {
            if (hubsResaltados.obtener(i).getProteina().equals(p)) return true;
        }
        return false;
    }

    private boolean estaEnComplejo(Proteina p) {
        for (int c = 0; c < complejosResaltados.tamanio(); c++) {
            ArregloDinamico<Proteina> prots = complejosResaltados.obtener(c).getProteinas();
            for (int i = 0; i < prots.tamanio(); i++) {
                if (prots.obtener(i).equals(p)) return true;
            }
        }
        return false;
    }

    public void resaltarComplejo(ComplejoProteinico c) {
        complejosResaltados.limpiar();
        if (c != null) complejosResaltados.agregar(c);
        repaint();
    }

    public void resaltarComplejos(ArregloDinamico<ComplejoProteinico> lista) {
        complejosResaltados = lista != null ? lista : new ArregloDinamico<>();
        repaint();
    }

    public void resaltarHubs(ArregloDinamico<ProteinaHub> lista) {
        hubsResaltados = lista != null ? lista : new ArregloDinamico<>();
        repaint();
    }

    public void resaltarRuta(ArregloDinamico<Proteina> ruta) {
        rutaResaltada = ruta != null ? ruta : new ArregloDinamico<>();
        repaint();
    }

    private Proteina obtenerNodoEn(int x, int y) {
        if (red == null) return null;
        double escala = zoom;
        ArregloDinamico<Proteina> vertices = red.getGrafo().getVertices();
        for (int i = 0; i < vertices.tamanio(); i++) {
            Proteina p = vertices.obtener(i);
            int[] pos = posicionesNodos.obtener(p);
            if (pos == null) continue;
            double dx = x / escala - pos[0];
            double dy = y / escala - pos[1];
            if (dx * dx + dy * dy <= (RADIO_NODO + 5) * (RADIO_NODO + 5)) return p;
        }
        return null;
    }
}
