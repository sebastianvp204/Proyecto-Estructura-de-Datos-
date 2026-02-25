package interfaz;

import controlador.ControladorPrincipal;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class VentanaPrincipal extends JFrame {

    private PanelVisualizacion panelVisualizacion;
    private PanelControl panelControl;
    private ControladorPrincipal controlador;

    public VentanaPrincipal() {
        setTitle("BioGraph - Red de proteínas");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarAplicacion();
            }
        });
        controlador = new ControladorPrincipal();
        inicializarComponentes();
        configurarMenu();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        panelVisualizacion = new PanelVisualizacion();
        panelControl = new PanelControl();
        panelControl.setControlador(controlador);
        panelControl.setPanelVisualizacion(panelVisualizacion);
        add(panelVisualizacion, BorderLayout.CENTER);
        add(panelControl, BorderLayout.EAST);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void configurarMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemCargar = new JMenuItem("Cargar datos");
        itemCargar.addActionListener(e -> panelControl.cargarArchivo());
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> cerrarAplicacion());
        menuArchivo.add(itemCargar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        barra.add(menuArchivo);

        JMenu menuHerramientas = new JMenu("Herramientas");
        JMenuItem itemComplejos = new JMenuItem("Detectar complejos");
        itemComplejos.addActionListener(e -> panelControl.ejecutarDeteccionComplejos());
        JMenuItem itemHubs = new JMenuItem("Identificar hubs");
        itemHubs.addActionListener(e -> panelControl.ejecutarIdentificacionHubs());
        JMenuItem itemRuta = new JMenuItem("Calcular ruta más corta");
        itemRuta.addActionListener(e -> panelControl.calcularRuta());
        menuHerramientas.add(itemComplejos);
        menuHerramientas.add(itemHubs);
        menuHerramientas.add(itemRuta);
        barra.add(menuHerramientas);

        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(e -> mostrarEstadisticas());
        menuAyuda.add(itemAcerca);
        barra.add(menuAyuda);

        setJMenuBar(barra);
    }

    public void cargarArchivo() {
        panelControl.cargarArchivo();
    }

    public void mostrarEstadisticas() {
        String stats = controlador.obtenerEstadisticas();
        JOptionPane.showMessageDialog(this, stats.isEmpty() ? "Cargue datos primero." : stats, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
    }

    public void actualizarVisualizacion() {
        if (panelVisualizacion != null) {
            panelVisualizacion.setRed(controlador.getRed());
        }
    }

    private void cerrarAplicacion() {
        if (JOptionPane.showConfirmDialog(this, "¿Salir de BioGraph?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}
