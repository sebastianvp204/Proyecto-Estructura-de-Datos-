package interfaz;

import controlador.ControladorPrincipal;
import dominio.ComplejoProteinico;
import dominio.Proteina;
import dominio.ProteinaHub;
import utilidades.ArregloDinamico;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class PanelControl extends JPanel {

    private final JComboBox<String> comboOrigen;
    private final JComboBox<String> comboDestino;
    private final JTextArea areaResultados;
    private final JButton btnCargar;
    private final JButton btnComplejos;
    private final JButton btnHubs;
    private final JButton btnRuta;
    private final JButton btnUsarSeleccionOrigen;
    private final JButton btnUsarSeleccionDestino;
    private final JLabel labelSeleccionado;
    private ControladorPrincipal controlador;
    private PanelVisualizacion panelVisualizacion;
    private String rutaProteinas;
    private String rutaInteracciones;
    private Proteina proteinaSeleccionada;

    public PanelControl() {
        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(280, 600));
        
        // Componentes
        comboOrigen = new JComboBox<>();
        comboDestino = new JComboBox<>();
        comboOrigen.setPreferredSize(new Dimension(150, 25));
        comboDestino.setPreferredSize(new Dimension(150, 25));
        
        areaResultados = new JTextArea(10, 20);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaResultados.setBackground(new Color(250, 250, 250));
        
        btnCargar = crearBoton("Cargar datos", new Color(220, 220, 220));
        btnComplejos = crearBoton("Detectar complejos", new Color(220, 220, 220));
        btnHubs = crearBoton("Identificar hubs", new Color(220, 220, 220));
        btnRuta = crearBoton("Calcular ruta", new Color(220, 220, 220));
        
        btnUsarSeleccionOrigen = new JButton("< Usar");
        btnUsarSeleccionDestino = new JButton("< Usar");
        btnUsarSeleccionOrigen.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnUsarSeleccionDestino.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnUsarSeleccionOrigen.setToolTipText("Usar proteína seleccionada en el grafo como origen");
        btnUsarSeleccionDestino.setToolTipText("Usar proteína seleccionada en el grafo como destino");
        
        labelSeleccionado = new JLabel("Ninguna");
        labelSeleccionado.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        rutaProteinas = null;
        rutaInteracciones = null;
        proteinaSeleccionada = null;
        
        inicializarControles();
        configurarListeners();
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return btn;
    }

    private void inicializarControles() {
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        
        // Panel de botones principales
        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 5, 8));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Acciones"));
        panelBotones.add(btnCargar);
        panelBotones.add(btnComplejos);
        panelBotones.add(btnHubs);
        panelBotones.add(btnRuta);
        panelNorte.add(panelBotones);
        
        panelNorte.add(Box.createVerticalStrut(10));
        
        // Panel de selección de proteína desde el grafo
        JPanel panelSeleccion = new JPanel(new BorderLayout(5, 5));
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Selección en grafo"));
        JPanel innerSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innerSeleccion.add(new JLabel("Seleccionada: "));
        innerSeleccion.add(labelSeleccionado);
        panelSeleccion.add(innerSeleccion, BorderLayout.CENTER);
        JLabel instruccion = new JLabel("<html><i>Haz clic en un nodo del grafo</i></html>");
        instruccion.setFont(new Font("SansSerif", Font.PLAIN, 10));
        instruccion.setForeground(Color.GRAY);
        panelSeleccion.add(instruccion, BorderLayout.SOUTH);
        panelNorte.add(panelSeleccion);
        
        panelNorte.add(Box.createVerticalStrut(10));
        
        // Panel de ruta
        JPanel panelRuta = new JPanel(new GridBagLayout());
        panelRuta.setBorder(BorderFactory.createTitledBorder("Calcular ruta más corta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Origen
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panelRuta.add(new JLabel("Origen:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panelRuta.add(comboOrigen, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panelRuta.add(btnUsarSeleccionOrigen, gbc);
        
        // Destino
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panelRuta.add(new JLabel("Destino:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panelRuta.add(comboDestino, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        panelRuta.add(btnUsarSeleccionDestino, gbc);
        
        panelNorte.add(panelRuta);
        
        add(panelNorte, BorderLayout.NORTH);
        
        // Panel de resultados
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        JScrollPane scroll = new JScrollPane(areaResultados);
        scroll.setPreferredSize(new Dimension(250, 200));
        panelResultados.add(scroll, BorderLayout.CENTER);
        add(panelResultados, BorderLayout.CENTER);
    }

    private void configurarListeners() {
        btnCargar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarArchivo();
            }
        });
        btnComplejos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarDeteccionComplejos();
            }
        });
        btnHubs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarIdentificacionHubs();
            }
        });
        btnRuta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calcularRuta();
            }
        });
        btnUsarSeleccionOrigen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (proteinaSeleccionada != null) {
                    comboOrigen.setSelectedItem(proteinaSeleccionada.getId());
                }
            }
        });
        btnUsarSeleccionDestino.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (proteinaSeleccionada != null) {
                    comboDestino.setSelectedItem(proteinaSeleccionada.getId());
                }
            }
        });
    }

    public void setControlador(ControladorPrincipal controlador) {
        this.controlador = controlador;
    }

    public void setPanelVisualizacion(PanelVisualizacion panelVisualizacion) {
        this.panelVisualizacion = panelVisualizacion;
        // Escuchar selección de nodos en el grafo
        if (panelVisualizacion != null) {
            panelVisualizacion.setListenerSeleccion(new PanelVisualizacion.ListenerSeleccionNodo() {
                @Override
                public void nodoSeleccionado(Proteina proteina) {
                    proteinaSeleccionada = proteina;
                    if (proteina != null) {
                        labelSeleccionado.setText(proteina.getId() + " (" + proteina.getNombre() + ")");
                        labelSeleccionado.setForeground(new Color(0, 100, 0));
                    } else {
                        labelSeleccionado.setText("Ninguna");
                        labelSeleccionado.setForeground(Color.BLACK);
                    }
                }
            });
        }
    }

    public void cargarArchivo() {
        if (controlador == null) return;
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) frame = new Frame();
        
        FilenameFilter filtroTxt = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        };
        
        // Diálogo para proteínas
        FileDialog dialogoProteinas = new FileDialog(frame, "Seleccionar archivo de proteínas", FileDialog.LOAD);
        dialogoProteinas.setFilenameFilter(filtroTxt);
        dialogoProteinas.setDirectory(new File(".").getAbsolutePath());
        dialogoProteinas.setVisible(true);
        
        if (dialogoProteinas.getFile() == null) return;
        rutaProteinas = dialogoProteinas.getDirectory() + dialogoProteinas.getFile();
        
        // Diálogo para interacciones
        FileDialog dialogoInteracciones = new FileDialog(frame, "Seleccionar archivo de interacciones", FileDialog.LOAD);
        dialogoInteracciones.setFilenameFilter(filtroTxt);
        dialogoInteracciones.setDirectory(dialogoProteinas.getDirectory());
        dialogoInteracciones.setVisible(true);
        
        if (dialogoInteracciones.getFile() == null) return;
        rutaInteracciones = dialogoInteracciones.getDirectory() + dialogoInteracciones.getFile();
        
        ejecutarCargaDatos();
    }

    private void ejecutarCargaDatos() {
        try {
            controlador.cargarRedProteinas(rutaProteinas, rutaInteracciones);
            if (panelVisualizacion != null) {
                panelVisualizacion.setRed(controlador.getRed());
            }
            actualizarCombosProteinas();
            String stats = controlador.obtenerEstadisticas();
            actualizarResultados(stats);
            String msgArchivos = "Archivos cargados:\n" +
                    "  Proteínas: " + new File(rutaProteinas).getName() + "\n" +
                    "  Interacciones: " + new File(rutaInteracciones).getName() + "\n\n" +
                    stats;
            JOptionPane.showMessageDialog(this, msgArchivos, "Datos cargados", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarCombosProteinas() {
        if (controlador == null || controlador.getRed() == null) return;
        ArregloDinamico<Proteina> proteinas = controlador.getRed().getGrafo().getVertices();
        String[] ids = new String[proteinas.tamanio()];
        for (int i = 0; i < proteinas.tamanio(); i++) {
            ids[i] = proteinas.obtener(i).getId();
        }
        comboOrigen.setModel(new DefaultComboBoxModel<>(ids));
        comboDestino.setModel(new DefaultComboBoxModel<>(ids));
        if (ids.length > 1) {
            comboOrigen.setSelectedIndex(0);
            comboDestino.setSelectedIndex(ids.length - 1);
        }
    }

    public void ejecutarDeteccionComplejos() {
        if (controlador == null || controlador.getRed() == null) {
            JOptionPane.showMessageDialog(this, "Primero cargue los datos.");
            return;
        }
        ArregloDinamico<ComplejoProteinico> complejos = controlador.ejecutarDeteccionComplejos();
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPLEJOS DETECTADOS ===\n");
        sb.append("Total: ").append(complejos.tamanio()).append("\n\n");
        for (int i = 0; i < complejos.tamanio(); i++) {
            ComplejoProteinico c = complejos.obtener(i);
            sb.append(c.getId()).append(":\n");
            sb.append("  Densidad: ").append(String.format("%.2f", c.getDensidad())).append("\n");
            sb.append("  Tamaño: ").append(c.getTamanio()).append(" proteínas\n");
            sb.append("  Miembros: ");
            ArregloDinamico<Proteina> miembros = c.getProteinas();
            for (int j = 0; j < miembros.tamanio(); j++) {
                sb.append(miembros.obtener(j).getId());
                if (j < miembros.tamanio() - 1) sb.append(", ");
            }
            sb.append("\n\n");
        }
        actualizarResultados(sb.toString());
        if (panelVisualizacion != null) {
            panelVisualizacion.resaltarComplejos(complejos);
        }
    }

    public void ejecutarIdentificacionHubs() {
        if (controlador == null || controlador.getRed() == null) {
            JOptionPane.showMessageDialog(this, "Primero cargue los datos.");
            return;
        }
        ArregloDinamico<ProteinaHub> hubs = controlador.ejecutarIdentificacionHubs();
        StringBuilder sb = new StringBuilder();
        sb.append("=== HUBS IDENTIFICADOS ===\n");
        sb.append("Total: ").append(hubs.tamanio()).append("\n\n");
        for (int i = 0; i < hubs.tamanio(); i++) {
            ProteinaHub h = hubs.obtener(i);
            sb.append(h.getProteina().getId()).append(" (").append(h.getProteina().getNombre()).append(")\n");
            sb.append("  Grado: ").append(h.getGradoCentralidad()).append(" conexiones\n");
            sb.append("  Importancia: ").append(String.format("%.2f", h.getImportancia())).append("\n\n");
        }
        if (hubs.tamanio() == 0) {
            sb.append("No se encontraron hubs con el umbral actual.\n");
            sb.append("(Un hub tiene grado > promedio + 2·σ)\n");
        }
        actualizarResultados(sb.toString());
        if (panelVisualizacion != null) {
            panelVisualizacion.resaltarHubs(hubs);
        }
    }

    public void calcularRuta() {
        if (controlador == null || controlador.getRed() == null) {
            JOptionPane.showMessageDialog(this, "Primero cargue los datos.");
            return;
        }
        Object selOrigen = comboOrigen.getSelectedItem();
        Object selDestino = comboDestino.getSelectedItem();
        if (selOrigen == null || selDestino == null) {
            JOptionPane.showMessageDialog(this, "Seleccione proteína origen y destino.");
            return;
        }
        String idOrigen = selOrigen.toString();
        String idDestino = selDestino.toString();
        if (idOrigen.equals(idDestino)) {
            JOptionPane.showMessageDialog(this, "Origen y destino deben ser diferentes.");
            return;
        }
        
        ArregloDinamico<Proteina> ruta = controlador.calcularRutaMasCorta(idOrigen, idDestino);
        StringBuilder sb = new StringBuilder();
        sb.append("=== RUTA MÁS CORTA ===\n");
        sb.append("De: ").append(idOrigen).append("\n");
        sb.append("A: ").append(idDestino).append("\n\n");
        
        if (ruta.tamanio() == 0) {
            sb.append("No existe camino entre estas proteínas.\n");
        } else {
            sb.append("Longitud: ").append(ruta.tamanio() - 1).append(" aristas\n\n");
            sb.append("Camino:\n");
            for (int i = 0; i < ruta.tamanio(); i++) {
                Proteina p = ruta.obtener(i);
                sb.append("  ").append(i + 1).append(". ").append(p.getId());
                sb.append(" (").append(p.getNombre()).append(")");
                if (i < ruta.tamanio() - 1) sb.append(" →");
                sb.append("\n");
            }
        }
        actualizarResultados(sb.toString());
        if (panelVisualizacion != null) {
            panelVisualizacion.resaltarRuta(ruta);
        }
    }

    public void actualizarResultados(String datos) {
        areaResultados.setText(datos != null ? datos : "");
        areaResultados.setCaretPosition(0);
    }

    public void limpiarResultados() {
        areaResultados.setText("");
    }
}
