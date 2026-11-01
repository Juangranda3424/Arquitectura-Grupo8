package ec.edu.monster.vista;

import ec.edu.monster.service.CuentaService;
import ec.edu.monster.ws.CuentaModel;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de retiro: consulta la cuenta, valida el saldo y registra el
 * movimiento RET.
 */
public class RetiroView extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(RetiroView.class.getName());

    private final CuentaService cuentaService = new CuentaService();
    private CuentaModel cuentaActual = null;
    private String numeroCuentaActual = null;

    // UI
    private JPanel panelBackground;
    private Tema.Tarjeta panelCard;
    private JTextField txtNumeroCuenta;
    private JTextField txtMonto;
    private JButton btnBuscarCuenta;
    private JButton btnRetirar;
    private JButton btnVolverMenu;
    private Tema.Mensaje mensaje;

    private JLabel lblCuentaInfo;
    private JLabel lblSaldoInfo;

    public RetiroView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        panelBackground = Tema.fondo(1100, 650, "Retiro");
        getContentPane().add(panelBackground);

        panelCard = new Tema.Tarjeta();
        panelCard.setBounds(60, 140, 980, 440);
        panelBackground.add(panelCard);

        JLabel lblTitulo = Tema.titulo("Realizar retiro");
        lblTitulo.setBounds(48, 32, 500, 32);
        panelCard.add(lblTitulo);

        JLabel lblAyuda = Tema.texto("Consulte la cuenta y retire el monto disponible.");
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

        lblCuentaInfo = Tema.texto(" ");
        lblCuentaInfo.setFont(Tema.fuente(Font.PLAIN, 12));
        lblCuentaInfo.setForeground(Tema.TEXTO_SUAVE);
        lblCuentaInfo.setBounds(48, 220, 500, 18);
        panelCard.add(lblCuentaInfo);

        lblSaldoInfo = Tema.valor(" ", 14, Tema.AZUL_MARINO);
        lblSaldoInfo.setBounds(48, 238, 500, 20);
        panelCard.add(lblSaldoInfo);

        JLabel lblMonto = Tema.etiqueta("MONTO A RETIRAR");
        lblMonto.setBounds(48, 268, 300, 16);
        panelCard.add(lblMonto);

        txtMonto = Tema.campo();
        txtMonto.setBounds(48, 288, 560, 42);
        panelCard.add(txtMonto);

        btnRetirar = Tema.botonPrimario("Realizar retiro");
        btnRetirar.setBounds(624, 288, 308, 46);
        panelCard.add(btnRetirar);

        btnVolverMenu = Tema.botonSecundario("Volver al menú");
        btnVolverMenu.setBounds(48, 360, 200, 42);
        panelCard.add(btnVolverMenu);

        // ===== LISTENERS =====
        btnBuscarCuenta.addActionListener(e -> buscarCuenta());
        txtNumeroCuenta.addActionListener(e -> buscarCuenta());
        btnRetirar.addActionListener(e -> realizarRetiro());
        txtMonto.addActionListener(e -> realizarRetiro());
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
            limpiarInfoCuenta();
            return;
        }

        try {
            CuentaModel cuentaModel = cuentaService.obtenerCuentaPorNumero(numeroCuenta);

            if (cuentaModel == null) {
                mensaje.error("No se encontraron datos para la cuenta: " + numeroCuenta);
                cuentaActual = null;
                numeroCuentaActual = null;
                limpiarInfoCuenta();
                return;
            }

            cuentaActual = cuentaModel;
            numeroCuentaActual = numeroCuenta;

            lblCuentaInfo.setText("Cuenta: " + numeroCuenta);
            lblSaldoInfo.setText("Saldo disponible: $ "
                    + String.format("%.2f", cuentaModel.getDecCuenSaldo()));

            txtMonto.setText("");
            mensaje.exito("Cuenta encontrada. Ingrese el monto a retirar.");

        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al obtener los datos de la cuenta", ex);
            mensaje.error("Error al obtener los datos de la cuenta: " + ex.getMessage());
            cuentaActual = null;
            numeroCuentaActual = null;
            limpiarInfoCuenta();
        }
    }

    private void realizarRetiro() {
        mensaje.limpiar();

        if (cuentaActual == null || numeroCuentaActual == null) {
            mensaje.error("Primero debe consultar una cuenta válida.");
            return;
        }

        String montoStr = clean(txtMonto.getText());
        if (montoStr.isEmpty()) {
            mensaje.error("Por favor, ingrese un monto a retirar.");
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr);
            if (monto <= 0) {
                mensaje.error("El monto debe ser mayor a cero.");
                return;
            }

            // Obtener saldo actualizado
            CuentaModel cuentaActualizada = cuentaService.obtenerCuentaPorNumero(numeroCuentaActual);
            if (cuentaActualizada == null) {
                mensaje.error("No se pudo obtener la información actualizada de la cuenta.");
                return;
            }
            cuentaActual = cuentaActualizada;

            double saldoActual = cuentaActual.getDecCuenSaldo();
            lblSaldoInfo.setText("Saldo disponible: $ " + String.format("%.2f", saldoActual));

            if (saldoActual < monto) {
                mensaje.error("Saldo insuficiente. Disponible: $ "
                        + String.format("%.2f", saldoActual)
                        + " | Solicitado: $ " + String.format("%.2f", monto));
                return;
            }

            boolean exito = cuentaService.realizarRetiro(numeroCuentaActual, montoStr);

            if (exito) {
                mensaje.exito("Retiro realizado con éxito.");
                CuentaModel despues = cuentaService.obtenerCuentaPorNumero(numeroCuentaActual);
                if (despues != null) {
                    cuentaActual = despues;
                    lblSaldoInfo.setText("Saldo disponible: $ "
                            + String.format("%.2f", despues.getDecCuenSaldo()));
                }
                txtMonto.setText("");
            } else {
                mensaje.error("Error al realizar el retiro. Verifique que tenga fondos suficientes.");
            }

        } catch (NumberFormatException e) {
            mensaje.error("Por favor, ingrese un monto válido (número).");
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al realizar el retiro", e);
            mensaje.error("Error al realizar el retiro: " + e.getMessage());
        }
    }

    private void limpiarInfoCuenta() {
        lblCuentaInfo.setText(" ");
        lblSaldoInfo.setText(" ");
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

        EventQueue.invokeLater(() -> new RetiroView().setVisible(true));
    }
}
