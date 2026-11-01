package ec.edu.monster.view;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.service.CuentaService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Pantalla de consulta de datos de una cuenta.
 */
public class DatosView extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(DatosView.class.getName());

    private final CuentaService cuentaService = new CuentaService();

    private JTextField campoNumeroCuenta;
    private JLabel valorCuenta;
    private JLabel valorSaldo;
    private JLabel valorContador;

    public DatosView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("EurekaBank · Datos de Cuenta");
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

        JLabel titulo = Tema.titulo("Consultar Datos de Cuenta");
        titulo.setBounds(44, 30, 700, 34);
        tarjeta.add(titulo);

        JLabel ayuda = Tema.ayuda("Saldo y contador de movimientos registrados en la cuenta");
        ayuda.setBounds(44, 68, 800, 20);
        tarjeta.add(ayuda);

        JLabel lblCuenta = Tema.etiqueta("Número de cuenta");
        lblCuenta.setBounds(44, 122, 400, 16);
        tarjeta.add(lblCuenta);

        campoNumeroCuenta = new JTextField();
        Tema.estilizarCampo(campoNumeroCuenta);
        campoNumeroCuenta.setBounds(44, 142, 912, 44);
        tarjeta.add(campoNumeroCuenta);

        JButton botonConsultar = Tema.botonPrimario("Consultar");
        botonConsultar.setBounds(44, 198, 912, 46);
        tarjeta.add(botonConsultar);

        JPanel panelDatos = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.NAVY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        panelDatos.setOpaque(false);
        panelDatos.setBounds(44, 266, 912, 168);
        tarjeta.add(panelDatos);

        JLabel lblInfo = new JLabel("Información de la cuenta");
        lblInfo.setFont(Tema.fuente(Font.BOLD, 16));
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setBounds(28, 22, 400, 24);
        panelDatos.add(lblInfo);

        JLabel lblCodigo = Tema.etiqueta("Código de cuenta");
        lblCodigo.setBounds(28, 66, 250, 16);
        lblCodigo.setForeground(new Color(0x9FB3CC));
        panelDatos.add(lblCodigo);

        valorCuenta = new JLabel("-");
        valorCuenta.setFont(Tema.fuente(Font.BOLD, 16));
        valorCuenta.setForeground(Color.WHITE);
        valorCuenta.setBounds(28, 86, 250, 24);
        panelDatos.add(valorCuenta);

        JLabel lblSaldo = Tema.etiqueta("Saldo");
        lblSaldo.setBounds(320, 66, 250, 16);
        lblSaldo.setForeground(new Color(0x9FB3CC));
        panelDatos.add(lblSaldo);

        valorSaldo = new JLabel("-");
        valorSaldo.setFont(Tema.fuente(Font.BOLD, 22));
        valorSaldo.setForeground(Tema.AMBER);
        valorSaldo.setBounds(320, 84, 250, 28);
        panelDatos.add(valorSaldo);

        JLabel lblContador = Tema.etiqueta("Contador de movimientos");
        lblContador.setBounds(620, 66, 270, 16);
        lblContador.setForeground(new Color(0x9FB3CC));
        panelDatos.add(lblContador);

        valorContador = new JLabel("-");
        valorContador.setFont(Tema.fuente(Font.BOLD, 22));
        valorContador.setForeground(Color.WHITE);
        valorContador.setBounds(620, 84, 270, 28);
        panelDatos.add(valorContador);

        JButton botonVolver = Tema.botonSecundario("Volver al Menú");
        botonVolver.setBounds(396, 452, 220, 44);
        tarjeta.add(botonVolver);

        botonConsultar.addActionListener(e -> buscarCuenta());
        campoNumeroCuenta.addActionListener(e -> buscarCuenta());
        botonVolver.addActionListener(e -> volverAlMenu());
        volver.addActionListener(e -> volverAlMenu());

        setContentPane(fondo);
    }

    private void volverAlMenu() {
        dispose();
        MenuView menu = new MenuView();
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
    }

    private void buscarCuenta() {
        String numeroCuenta = campoNumeroCuenta.getText() == null ? "" : campoNumeroCuenta.getText().trim();

        if (numeroCuenta.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese un número de cuenta.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CuentaModel cuentaModel = cuentaService.obtenerCuentaPorNumero(numeroCuenta);

            if (cuentaModel == null) {
                limpiar();
                JOptionPane.showMessageDialog(this,
                        "No se encontraron datos para la cuenta: " + numeroCuenta,
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            valorCuenta.setText(numeroCuenta);
            valorSaldo.setText(String.format("$ %.2f", cuentaModel.getDecCuenSaldo()));
            valorContador.setText(String.valueOf(cuentaModel.getIntCuenContMov()));
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al obtener los datos de la cuenta", ex);
            JOptionPane.showMessageDialog(this,
                    "Error al obtener los datos de la cuenta: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            limpiar();
        }
    }

    private void limpiar() {
        valorCuenta.setText("-");
        valorSaldo.setText("-");
        valorContador.setText("-");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new DatosView().setVisible(true));
    }
}
