package ec.edu.monster.vista;

import ec.edu.monster.controlador.MovimientoController;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de movimientos de una cuenta: buscador, listado en tarjetas y
 * resumen de totales.
 */
public class MovimientoView extends JFrame {

    private JPanel panelBackground;
    private Tema.Tarjeta panelCard;

    private JTextField txtCuenta;
    private JButton btnBuscar;
    private JButton btnVolverMenu;
    private Tema.Mensaje mensaje;

    private JScrollPane jScrollPane1;

    public MovimientoView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(null);

        panelBackground = Tema.fondo(1100, 650, "Movimientos");
        getContentPane().add(panelBackground);

        panelCard = new Tema.Tarjeta();
        panelCard.setBounds(60, 130, 980, 480);
        panelBackground.add(panelCard);

        JLabel lblTitulo = Tema.titulo("Consultar movimientos");
        lblTitulo.setBounds(40, 24, 500, 32);
        panelCard.add(lblTitulo);

        mensaje = new Tema.Mensaje();
        mensaje.setBounds(40, 60, 900, 38);
        panelCard.add(mensaje);

        JLabel lblNumCuenta = Tema.etiqueta("NÚMERO DE CUENTA");
        lblNumCuenta.setBounds(40, 106, 300, 16);
        panelCard.add(lblNumCuenta);

        txtCuenta = Tema.campo();
        txtCuenta.setBounds(40, 126, 560, 40);
        panelCard.add(txtCuenta);

        btnBuscar = Tema.botonPrimario("Consultar movimientos");
        btnBuscar.setBounds(616, 126, 324, 42);
        panelCard.add(btnBuscar);

        jScrollPane1 = new JScrollPane();
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(Tema.FONDO);
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);

        JPanel panelResultados = new JPanel();
        panelResultados.setLayout(new BoxLayout(panelResultados, BoxLayout.Y_AXIS));
        panelResultados.setBackground(Tema.FONDO);
        jScrollPane1.setViewportView(panelResultados);

        jScrollPane1.setBounds(40, 184, 900, 220);
        panelCard.add(jScrollPane1);

        btnVolverMenu = Tema.botonSecundario("Volver al menú");
        btnVolverMenu.setBounds(40, 416, 200, 42);
        panelCard.add(btnVolverMenu);

        // Consultar movimientos
        btnBuscar.addActionListener(e -> {
            MovimientoController controller = new MovimientoController();
            String cuenta = txtCuenta.getText();

            if (cuenta == null || cuenta.trim().isEmpty()) {
                mensaje.error("Por favor, ingrese un número de cuenta válido.");
                return;
            }

            mensaje.limpiar();
            controller.cargarMovimientos(cuenta, this);
        });

        // Enter = consultar
        txtCuenta.addActionListener(e -> btnBuscar.doClick());

        // Volver al menú
        btnVolverMenu.addActionListener(e -> dispose());

        setContentPane(panelBackground);
    }

    /**
     * Celdas de movimiento con el estilo de la identidad EurekaBank · GR8.
     * Usado desde {@link MovimientoController}.
     */
    public JPanel crearCelda(String cuenta,
                             String fecha,
                             String movimiento,
                             String descripcion,
                             String tipo,
                             String importe,
                             String saldoDespues) {

        JPanel panelCelda = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.SUPERFICIE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Tema.RADIO, Tema.RADIO);
                g2.setColor(Tema.BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Tema.RADIO, Tema.RADIO);
                g2.setColor(Tema.TEAL);
                g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };

        panelCelda.setLayout(new BorderLayout());
        panelCelda.setMaximumSize(new Dimension(880, 120));
        panelCelda.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JPanel datosPanel = new JPanel(new GridLayout(3, 2, 8, 4));
        datosPanel.setOpaque(false);

        JLabel lblCuenta = new JLabel(cuenta);
        lblCuenta.setFont(Tema.fuente(Font.BOLD, 13));
        lblCuenta.setForeground(Tema.AZUL_MARINO);

        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setFont(Tema.fuente(Font.PLAIN, 13));
        lblDescripcion.setForeground(Tema.TEXTO);

        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(Tema.fuente(Font.PLAIN, 12));
        lblFecha.setForeground(Tema.TEXTO_SUAVE);

        JLabel lblTipo = new JLabel(tipo);
        lblTipo.setFont(Tema.fuente(Font.PLAIN, 12));
        lblTipo.setForeground(Tema.TEXTO_SUAVE);

        JLabel lblMovimiento = new JLabel(movimiento);
        lblMovimiento.setFont(Tema.fuente(Font.PLAIN, 12));
        lblMovimiento.setForeground(Tema.TEXTO_SUAVE);

        JLabel lblImporte = new JLabel(importe);
        lblImporte.setFont(Tema.fuente(Font.BOLD, 15));
        lblImporte.setForeground(Tema.TEAL);

        JLabel lblSaldo = new JLabel(saldoDespues);
        lblSaldo.setFont(Tema.fuente(Font.BOLD, 13));
        lblSaldo.setForeground(Tema.EXITO);

        datosPanel.add(lblCuenta);
        datosPanel.add(lblDescripcion);
        datosPanel.add(lblFecha);
        datosPanel.add(lblTipo);
        datosPanel.add(lblMovimiento);
        datosPanel.add(lblImporte);

        panelCelda.add(datosPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(lblSaldo, BorderLayout.EAST);

        panelCelda.add(footer, BorderLayout.SOUTH);

        panelCelda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 6, 6, 6),
                panelCelda.getBorder()));

        return panelCelda;
    }

    // Getter para que MovimientoController pueda reemplazar el contenido del scroll
    public JScrollPane getjScrollPane1() {
        return jScrollPane1;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }

        EventQueue.invokeLater(() -> new MovimientoView().setVisible(true));
    }
}
