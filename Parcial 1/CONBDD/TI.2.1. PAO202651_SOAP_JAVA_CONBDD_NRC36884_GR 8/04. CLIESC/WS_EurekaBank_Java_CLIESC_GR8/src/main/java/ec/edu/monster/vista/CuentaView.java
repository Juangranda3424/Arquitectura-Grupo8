package ec.edu.monster.vista;

import ec.edu.monster.service.CuentaService;
import ec.edu.monster.ws.CuentaModel;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de consulta de datos de cuenta (saldo y contador de movimientos).
 */
public class CuentaView extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(CuentaView.class.getName());

    private final CuentaService cuentaService = new CuentaService();

    private JPanel panelBackground;
    private Tema.Tarjeta panelCard;
    private Tema.Tarjeta panelInfoCard;
    private Tema.Mensaje mensaje;
    private JTextField txtNumeroCuenta;
    private JButton btnBuscarCuenta;
    private JButton btnVolverMenu;

    private JLabel lblCuentaEncontrada;
    private JLabel lblSaldoEncontrado;
    private JLabel lblMovimientosEncontrado;

    public CuentaView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        panelBackground = Tema.fondo(1100, 650, "Datos de cuenta");
        getContentPane().add(panelBackground);

        panelCard = new Tema.Tarjeta();
        panelCard.setBounds(60, 140, 980, 460);
        panelBackground.add(panelCard);

        JLabel lblTitulo = Tema.titulo("Consultar datos de cuenta");
        lblTitulo.setBounds(48, 32, 600, 32);
        panelCard.add(lblTitulo);

        JLabel lblAyuda = Tema.texto("Ingrese el número de cuenta para ver saldo y contador de movimientos.");
        lblAyuda.setForeground(Tema.TEXTO_SUAVE);
        lblAyuda.setBounds(48, 66, 700, 20);
        panelCard.add(lblAyuda);

        mensaje = new Tema.Mensaje();
        mensaje.setBounds(48, 98, 884, 40);
        panelCard.add(mensaje);

        JLabel lblNumero = Tema.etiqueta("NÚMERO DE CUENTA");
        lblNumero.setBounds(48, 152, 300, 16);
        panelCard.add(lblNumero);

        txtNumeroCuenta = Tema.campo();
        txtNumeroCuenta.setBounds(48, 172, 560, 42);
        panelCard.add(txtNumeroCuenta);

        btnBuscarCuenta = Tema.botonPrimario("Consultar");
        btnBuscarCuenta.setBounds(624, 172, 308, 46);
        panelCard.add(btnBuscarCuenta);

        // TARJETA DE INFORMACIÓN (azul marino)
        panelInfoCard = new Tema.Tarjeta();
        panelInfoCard.setRelleno(Tema.AZUL_MARINO);
        panelInfoCard.setBounds(48, 240, 884, 130);
        panelCard.add(panelInfoCard);

        JLabel lblInfoTitle = new JLabel("Información de la cuenta");
        lblInfoTitle.setFont(Tema.fuente(Font.BOLD, 16));
        lblInfoTitle.setForeground(Color.WHITE);
        lblInfoTitle.setBounds(28, 18, 400, 22);
        panelInfoCard.add(lblInfoTitle);

        JLabel lblCodigoCuentaTitle = Tema.etiqueta("CÓDIGO DE CUENTA");
        lblCodigoCuentaTitle.setForeground(new Color(0x9F, 0xB0, 0xC4));
        lblCodigoCuentaTitle.setBounds(28, 56, 240, 16);
        panelInfoCard.add(lblCodigoCuentaTitle);

        lblCuentaEncontrada = new JLabel(" ");
        lblCuentaEncontrada.setFont(Tema.fuente(Font.BOLD, 18));
        lblCuentaEncontrada.setForeground(Color.WHITE);
        lblCuentaEncontrada.setBounds(28, 76, 240, 26);
        panelInfoCard.add(lblCuentaEncontrada);

        JLabel lblSaldoLabel = Tema.etiqueta("SALDO");
        lblSaldoLabel.setForeground(new Color(0x9F, 0xB0, 0xC4));
        lblSaldoLabel.setBounds(320, 56, 160, 16);
        panelInfoCard.add(lblSaldoLabel);

        lblSaldoEncontrado = new JLabel(" ");
        lblSaldoEncontrado.setFont(Tema.fuente(Font.BOLD, 22));
        lblSaldoEncontrado.setForeground(Tema.AMBAR);
        lblSaldoEncontrado.setBounds(320, 74, 220, 28);
        panelInfoCard.add(lblSaldoEncontrado);

        JLabel lblMovimientosLabel = Tema.etiqueta("CONTADOR DE MOVIMIENTOS");
        lblMovimientosLabel.setForeground(new Color(0x9F, 0xB0, 0xC4));
        lblMovimientosLabel.setBounds(620, 56, 240, 16);
        panelInfoCard.add(lblMovimientosLabel);

        lblMovimientosEncontrado = new JLabel(" ");
        lblMovimientosEncontrado.setFont(Tema.fuente(Font.BOLD, 18));
        lblMovimientosEncontrado.setForeground(Color.WHITE);
        lblMovimientosEncontrado.setBounds(620, 76, 240, 26);
        panelInfoCard.add(lblMovimientosEncontrado);

        // Inicialmente ocultar el panel de información
        panelInfoCard.setVisible(false);

        btnVolverMenu = Tema.botonSecundario("Volver al menú");
        btnVolverMenu.setBounds(48, 392, 200, 42);
        panelCard.add(btnVolverMenu);

        // ===== ACCIONES =====
        btnBuscarCuenta.addActionListener(e -> buscarCuenta());
        txtNumeroCuenta.addActionListener(e -> buscarCuenta());
        btnVolverMenu.addActionListener(e -> dispose());

        setContentPane(panelBackground);
    }

    private void buscarCuenta() {
        mensaje.limpiar();
        String numeroCuenta = txtNumeroCuenta.getText() != null
                ? txtNumeroCuenta.getText().trim()
                : "";

        if (numeroCuenta.isEmpty()) {
            mensaje.error("Por favor, ingrese un número de cuenta.");
            panelInfoCard.setVisible(false);
            return;
        }

        try {
            CuentaModel cuentaModel = cuentaService.obtenerCuentaPorNumero(numeroCuenta);

            if (cuentaModel == null) {
                limpiarLabels();
                panelInfoCard.setVisible(false);
                mensaje.error("No se encontraron datos para la cuenta: " + numeroCuenta);
                return;
            }

            lblCuentaEncontrada.setText(numeroCuenta);
            lblSaldoEncontrado.setText(String.format("$ %.2f", cuentaModel.getDecCuenSaldo()));
            lblMovimientosEncontrado.setText(String.valueOf(cuentaModel.getIntCuenContMov()));
            panelInfoCard.setVisible(true);
            mensaje.exito("Datos de la cuenta " + numeroCuenta + " obtenidos correctamente.");

        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE,
                    "Error al obtener los datos de la cuenta", ex);
            mensaje.error("Error al obtener los datos de la cuenta: " + ex.getMessage());
            limpiarLabels();
            panelInfoCard.setVisible(false);
        }
    }

    private void limpiarLabels() {
        lblCuentaEncontrada.setText(" ");
        lblSaldoEncontrado.setText(" ");
        lblMovimientosEncontrado.setText(" ");
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        EventQueue.invokeLater(() -> new CuentaView().setVisible(true));
    }
}
