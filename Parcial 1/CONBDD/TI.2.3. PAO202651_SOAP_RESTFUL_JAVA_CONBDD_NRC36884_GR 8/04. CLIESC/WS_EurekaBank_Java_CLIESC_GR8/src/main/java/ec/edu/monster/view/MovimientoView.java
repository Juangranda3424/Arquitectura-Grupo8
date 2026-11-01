package ec.edu.monster.view;

import ec.edu.monster.controller.MovimientoController;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Pantalla de consulta de movimientos de una cuenta.
 */
public class MovimientoView extends JFrame {

    private JTextField campoCuenta;
    private JScrollPane panelScroll;
    private final Tema.Banner banner = new Tema.Banner();

    public MovimientoView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("EurekaBank · Movimientos");
        setSize(1100, 680);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel fondo = new JPanel(null);
        fondo.setBackground(Tema.FONDO);
        fondo.setBounds(0, 0, 1100, 680);

        JPanel barra = Tema.cabecera();
        barra.setBounds(0, 0, 1100, 72);
        JButton volver = Tema.botonSecundario("Volver al Menú");
        volver.setPreferredSize(new Dimension(170, 38));
        JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        contenedor.setOpaque(false);
        contenedor.add(volver);
        barra.add(contenedor, BorderLayout.EAST);
        fondo.add(barra);

        JPanel tarjeta = Tema.tarjeta();
        tarjeta.setBounds(50, 104, 1000, 540);
        fondo.add(tarjeta);

        JLabel titulo = Tema.titulo("Consultar Movimientos");
        titulo.setBounds(44, 30, 700, 34);
        tarjeta.add(titulo);

        JLabel ayuda = Tema.ayuda("Historial de movimientos y saldo acumulado de la cuenta");
        ayuda.setBounds(44, 68, 800, 20);
        tarjeta.add(ayuda);

        banner.setBounds(44, 98, 912, 42);
        tarjeta.add(banner);

        JLabel lblCuenta = Tema.etiqueta("Número de cuenta");
        lblCuenta.setBounds(44, 152, 400, 16);
        tarjeta.add(lblCuenta);

        campoCuenta = new JTextField();
        Tema.estilizarCampo(campoCuenta);
        campoCuenta.setBounds(44, 172, 912, 44);
        tarjeta.add(campoCuenta);

        JButton botonConsultar = Tema.botonPrimario("Consultar Movimientos");
        botonConsultar.setBounds(44, 228, 912, 46);
        tarjeta.add(botonConsultar);

        JPanel panelResultados = new JPanel();
        panelResultados.setLayout(new BoxLayout(panelResultados, BoxLayout.Y_AXIS));
        panelResultados.setBackground(Tema.FONDO);

        panelScroll = new JScrollPane(panelResultados);
        panelScroll.setBorder(BorderFactory.createEmptyBorder());
        panelScroll.getViewport().setBackground(Tema.FONDO);
        panelScroll.getVerticalScrollBar().setUnitIncrement(16);
        panelScroll.setBounds(44, 288, 912, 182);
        tarjeta.add(panelScroll);

        JButton botonVolver = Tema.botonSecundario("Volver al Menú");
        botonVolver.setBounds(396, 482, 220, 44);
        tarjeta.add(botonVolver);

        botonConsultar.addActionListener(e -> consultarMovimientos());
        campoCuenta.addActionListener(e -> consultarMovimientos());
        botonVolver.addActionListener(e -> volverAlMenu());
        volver.addActionListener(e -> volverAlMenu());

        setContentPane(fondo);
    }

    private void consultarMovimientos() {
        banner.ocultar();
        String cuenta = campoCuenta.getText() == null ? "" : campoCuenta.getText().trim();

        if (cuenta.isEmpty()) {
            banner.error("Por favor, ingrese un número de cuenta válido.");
            return;
        }

        new MovimientoController().cargarMovimientos(cuenta, this);
    }

    private void volverAlMenu() {
        dispose();
        MenuView menu = new MenuView();
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
    }

    /**
     * Construye una celda de movimiento con la identidad visual de la marca.
     */
    public JPanel crearCelda(String cuenta,
                             String fecha,
                             String movimiento,
                             String descripcion,
                             String tipo,
                             String importe,
                             String saldoDespues) {

        JPanel celda = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.SUPERFICIE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.setColor(Tema.BORDE);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        celda.setOpaque(false);
        celda.setBorder(BorderFactory.createEmptyBorder(24, 14, 10, 14));
        celda.setMaximumSize(new Dimension(890, 104));

        JPanel datos = new JPanel(new GridLayout(3, 2, 10, 2));
        datos.setOpaque(false);

        JLabel lblCuenta = new JLabel(cuenta);
        lblCuenta.setFont(Tema.fuente(Font.BOLD, 13));
        lblCuenta.setForeground(Tema.NAVY);

        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setFont(Tema.fuente(Font.BOLD, 13));
        lblDescripcion.setForeground(Tema.TEAL);
        lblDescripcion.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(Tema.fuente(Font.PLAIN, 11));
        lblFecha.setForeground(Tema.MUTED);

        JLabel lblTipo = new JLabel(tipo);
        lblTipo.setFont(Tema.fuente(Font.PLAIN, 11));
        lblTipo.setForeground(Tema.MUTED);
        lblTipo.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblMovimiento = new JLabel(movimiento);
        lblMovimiento.setFont(Tema.fuente(Font.PLAIN, 11));
        lblMovimiento.setForeground(Tema.MUTED);

        JLabel lblImporte = new JLabel(importe);
        lblImporte.setFont(Tema.fuente(Font.BOLD, 15));
        lblImporte.setForeground(Tema.AMBER);
        lblImporte.setHorizontalAlignment(SwingConstants.RIGHT);

        datos.add(lblCuenta);
        datos.add(lblDescripcion);
        datos.add(lblFecha);
        datos.add(lblTipo);
        datos.add(lblMovimiento);
        datos.add(lblImporte);
        celda.add(datos, BorderLayout.CENTER);

        JLabel lblSaldo = new JLabel(saldoDespues);
        lblSaldo.setFont(Tema.fuente(Font.BOLD, 12));
        lblSaldo.setForeground(Tema.EXITO);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(lblSaldo, BorderLayout.EAST);
        celda.add(pie, BorderLayout.SOUTH);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        contenedor.add(celda, BorderLayout.CENTER);
        contenedor.setMaximumSize(new Dimension(898, 112));
        return contenedor;
    }

    public JScrollPane getjScrollPane1() {
        return panelScroll;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MovimientoView().setVisible(true));
    }
}
