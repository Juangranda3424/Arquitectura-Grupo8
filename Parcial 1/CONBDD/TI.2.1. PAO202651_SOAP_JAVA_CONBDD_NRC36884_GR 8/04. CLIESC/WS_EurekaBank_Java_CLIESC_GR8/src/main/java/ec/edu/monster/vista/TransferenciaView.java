package ec.edu.monster.vista;

import ec.edu.monster.service.CuentaService;
import ec.edu.monster.ws.CuentaModel;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de transferencia entre cuentas: valida origen, destino y saldo, y
 * registra los movimientos 009 / 008 en el servidor.
 */
public class TransferenciaView extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(TransferenciaView.class.getName());

    private final CuentaService cuentaService = new CuentaService();

    // UI
    private JPanel panelBackground;
    private Tema.Tarjeta panelCard;
    private Tema.Mensaje mensaje;
    private JTextField txtCuentaOrigen;
    private JTextField txtCuentaDestino;
    private JTextField txtMonto;
    private JButton btnTransferir;
    private JButton btnVolverMenu;

    public TransferenciaView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        panelBackground = Tema.fondo(1100, 650, "Transferencia");
        getContentPane().add(panelBackground);

        panelCard = new Tema.Tarjeta();
        panelCard.setBounds(60, 140, 980, 440);
        panelBackground.add(panelCard);

        JLabel lblTitulo = Tema.titulo("Realizar transferencia");
        lblTitulo.setBounds(48, 32, 560, 32);
        panelCard.add(lblTitulo);

        JLabel lblAyuda = Tema.texto("El servidor debita la cuenta origen y acredita la cuenta destino.");
        lblAyuda.setForeground(Tema.TEXTO_SUAVE);
        lblAyuda.setBounds(48, 66, 700, 20);
        panelCard.add(lblAyuda);

        mensaje = new Tema.Mensaje();
        mensaje.setBounds(48, 98, 884, 40);
        panelCard.add(mensaje);

        JLabel lblOrigen = Tema.etiqueta("CUENTA ORIGEN");
        lblOrigen.setBounds(48, 152, 420, 16);
        panelCard.add(lblOrigen);

        txtCuentaOrigen = Tema.campo();
        txtCuentaOrigen.setBounds(48, 172, 420, 42);
        panelCard.add(txtCuentaOrigen);

        JLabel lblDestino = Tema.etiqueta("CUENTA DESTINO");
        lblDestino.setBounds(512, 152, 420, 16);
        panelCard.add(lblDestino);

        txtCuentaDestino = Tema.campo();
        txtCuentaDestino.setBounds(512, 172, 420, 42);
        panelCard.add(txtCuentaDestino);

        JLabel lblMonto = Tema.etiqueta("MONTO A TRANSFERIR");
        lblMonto.setBounds(48, 232, 420, 16);
        panelCard.add(lblMonto);

        txtMonto = Tema.campo();
        txtMonto.setBounds(48, 252, 420, 42);
        panelCard.add(txtMonto);

        btnTransferir = Tema.botonPrimario("Realizar transferencia");
        btnTransferir.setBounds(512, 252, 420, 46);
        panelCard.add(btnTransferir);

        JLabel lblNota = Tema.texto("Tipo de movimiento: 009 (salida) y 008 (entrada).");
        lblNota.setFont(Tema.fuente(Font.PLAIN, 12));
        lblNota.setForeground(Tema.TEXTO_SUAVE);
        lblNota.setBounds(48, 306, 884, 18);
        panelCard.add(lblNota);

        btnVolverMenu = Tema.botonSecundario("Volver al menú");
        btnVolverMenu.setBounds(48, 356, 200, 42);
        panelCard.add(btnVolverMenu);

        // ===== Listeners =====
        btnTransferir.addActionListener(e -> realizarTransferencia());
        txtMonto.addActionListener(e -> realizarTransferencia());
        btnVolverMenu.addActionListener(e -> dispose());

        setContentPane(panelBackground);
    }

    // ========= Lógica =========

    private void realizarTransferencia() {
        mensaje.limpiar();

        String origen = clean(txtCuentaOrigen.getText());
        String destino = clean(txtCuentaDestino.getText());
        String montoStr = clean(txtMonto.getText());

        if (origen.isEmpty() || destino.isEmpty() || montoStr.isEmpty()) {
            mensaje.error("Complete cuenta origen, cuenta destino y monto.");
            return;
        }

        if (origen.equals(destino)) {
            mensaje.error("La cuenta origen y destino no pueden ser la misma.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException ex) {
            mensaje.error("El monto debe ser un número válido.");
            return;
        }

        if (monto <= 0) {
            mensaje.error("El monto debe ser mayor a cero.");
            return;
        }

        try {
            // Validar cuentas
            CuentaModel cOrigen = cuentaService.obtenerCuentaPorNumero(origen);
            if (cOrigen == null) {
                mensaje.error("No se encontró la cuenta origen: " + origen);
                return;
            }

            CuentaModel cDestino = cuentaService.obtenerCuentaPorNumero(destino);
            if (cDestino == null) {
                mensaje.error("No se encontró la cuenta destino: " + destino);
                return;
            }

            // Validar saldo origen
            if (cOrigen.getDecCuenSaldo() < monto) {
                mensaje.error("Saldo insuficiente en cuenta origen. Disponible: $ "
                        + String.format("%.2f", cOrigen.getDecCuenSaldo()));
                return;
            }

            boolean exito = cuentaService.realizarTransferencia(origen, destino, montoStr);

            if (exito) {
                mensaje.exito("Transferencia realizada con éxito.");
                txtMonto.setText("");
            } else {
                mensaje.error("Error al realizar la transferencia. Verifique los datos o el saldo.");
            }

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al realizar la transferencia", e);
            mensaje.error("Error al realizar la transferencia: " + e.getMessage());
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

        EventQueue.invokeLater(() -> new TransferenciaView().setVisible(true));
    }
}
