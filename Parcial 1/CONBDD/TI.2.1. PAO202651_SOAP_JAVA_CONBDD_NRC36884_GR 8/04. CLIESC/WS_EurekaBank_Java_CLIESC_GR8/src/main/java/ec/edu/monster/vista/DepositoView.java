package ec.edu.monster.vista;

import ec.edu.monster.service.CuentaService;
import ec.edu.monster.ws.CuentaModel;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de depósito: consulta la cuenta y registra el movimiento DEP.
 */
public class DepositoView extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(DepositoView.class.getName());

    private final CuentaService cuentaService = new CuentaService();
    private CuentaModel cuentaActual = null;
    private String numeroCuentaActual = null;

    // UI
    private JPanel panelBackground;
    private Tema.Tarjeta panelCard;
    private JTextField txtNumeroCuenta;
    private JTextField txtMonto;
    private JButton btnBuscarCuenta;
    private JButton btnDepositar;
    private JButton btnVolverMenu;
    private Tema.Mensaje mensaje;
    private JLabel lblClienteInfo;

    public DepositoView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        panelBackground = Tema.fondo(1100, 650, "Depósito");
        getContentPane().add(panelBackground);

        panelCard = new Tema.Tarjeta();
        panelCard.setBounds(60, 140, 980, 440);
        panelBackground.add(panelCard);

        JLabel lblTitulo = Tema.titulo("Realizar depósito");
        lblTitulo.setBounds(48, 32, 500, 32);
        panelCard.add(lblTitulo);

        JLabel lblAyuda = Tema.texto("Consulte la cuenta y registre el monto a acreditar.");
        lblAyuda.setForeground(Tema.TEXTO_SUAVE);
        lblAyuda.setBounds(48, 66, 700, 20);
        panelCard.add(lblAyuda);

        mensaje = new Tema.Mensaje();
        mensaje.setBounds(48, 98, 884, 40);
        panelCard.add(mensaje);

        JLabel lblNumCuenta = Tema.etiqueta("NÚMERO DE CUENTA");
        lblNumCuenta.setBounds(48, 152, 300, 16);
        panelCard.add(lblNumCuenta);

        txtNumeroCuenta = Tema.campo();
        txtNumeroCuenta.setBounds(48, 172, 560, 42);
        panelCard.add(txtNumeroCuenta);

        btnBuscarCuenta = Tema.botonSecundario("Consultar cuenta");
        btnBuscarCuenta.setBounds(624, 172, 200, 42);
        panelCard.add(btnBuscarCuenta);

        JLabel lblMonto = Tema.etiqueta("MONTO");
        lblMonto.setBounds(48, 232, 300, 16);
        panelCard.add(lblMonto);

        txtMonto = Tema.campo();
        txtMonto.setBounds(48, 252, 560, 42);
        panelCard.add(txtMonto);

        btnDepositar = Tema.botonPrimario("Realizar depósito");
        btnDepositar.setBounds(624, 252, 308, 46);
        panelCard.add(btnDepositar);

        lblClienteInfo = Tema.texto(" ");
        lblClienteInfo.setFont(Tema.fuente(Font.PLAIN, 12));
        lblClienteInfo.setForeground(Tema.TEXTO_SUAVE);
        lblClienteInfo.setBounds(48, 306, 884, 18);
        panelCard.add(lblClienteInfo);

        btnVolverMenu = Tema.botonSecundario("Volver al menú");
        btnVolverMenu.setBounds(48, 356, 200, 42);
        panelCard.add(btnVolverMenu);

        // ===== LISTENERS =====
        btnBuscarCuenta.addActionListener(e -> buscarCuenta());
        txtNumeroCuenta.addActionListener(e -> buscarCuenta());
        btnDepositar.addActionListener(e -> realizarDeposito());
        txtMonto.addActionListener(e -> realizarDeposito());
        btnVolverMenu.addActionListener(e -> dispose());

        setContentPane(panelBackground);
    }

    // ========= LÓGICA =========

    private void buscarCuenta() {
        mensaje.limpiar();
        String numeroCuenta = clean(txtNumeroCuenta.getText());

        if (numeroCuenta.isEmpty()) {
            mensaje.error("Por favor, ingrese un número de cuenta.");
            cuentaActual = null;
            numeroCuentaActual = null;
            return;
        }

        try {
            CuentaModel cuentaModel = cuentaService.obtenerCuentaPorNumero(numeroCuenta);

            if (cuentaModel == null) {
                mensaje.error("No se encontraron datos para la cuenta: " + numeroCuenta);
                cuentaActual = null;
                numeroCuentaActual = null;
                lblClienteInfo.setText(" ");
                return;
            }

            cuentaActual = cuentaModel;
            numeroCuentaActual = numeroCuenta;
            lblClienteInfo.setText("Cuenta " + numeroCuenta + " · saldo actual $ "
                    + String.format("%.2f", cuentaModel.getDecCuenSaldo()));

            mensaje.exito("Cuenta " + numeroCuenta + " encontrada. Ahora ingrese el monto a depositar.");

        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al obtener los datos de la cuenta", ex);
            mensaje.error("Error al obtener los datos de la cuenta: " + ex.getMessage());
            cuentaActual = null;
            numeroCuentaActual = null;
        }
    }

    private void realizarDeposito() {
        mensaje.limpiar();

        if (cuentaActual == null || numeroCuentaActual == null) {
            mensaje.error("Primero debe consultar una cuenta válida.");
            return;
        }

        String montoStr = clean(txtMonto.getText());
        if (montoStr.isEmpty()) {
            mensaje.error("Por favor, ingrese un monto a depositar.");
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr);
            if (monto <= 0) {
                mensaje.error("El monto debe ser mayor a cero.");
                return;
            }

            boolean exito = cuentaService.realizarDeposito(numeroCuentaActual, montoStr, "DEP", null);

            if (exito) {
                mensaje.exito("Depósito realizado con éxito.");
                CuentaModel actualizada = cuentaService.obtenerCuentaPorNumero(numeroCuentaActual);
                if (actualizada != null) {
                    cuentaActual = actualizada;
                    lblClienteInfo.setText("Cuenta " + numeroCuentaActual + " · saldo actual $ "
                            + String.format("%.2f", actualizada.getDecCuenSaldo()));
                }
                txtMonto.setText("");
            } else {
                mensaje.error("Error al realizar el depósito.");
            }

        } catch (NumberFormatException e) {
            mensaje.error("Por favor, ingrese un monto válido (número).");
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al realizar el depósito", e);
            mensaje.error("Error al realizar el depósito: " + e.getMessage());
        }
    }

    private String clean(String s) {
        return s == null ? "" : s.trim();
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        EventQueue.invokeLater(() -> new DepositoView().setVisible(true));
    }
}
