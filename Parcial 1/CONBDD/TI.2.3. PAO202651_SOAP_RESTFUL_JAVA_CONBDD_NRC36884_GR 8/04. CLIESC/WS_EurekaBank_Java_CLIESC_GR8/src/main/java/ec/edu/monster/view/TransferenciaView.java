package ec.edu.monster.view;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.service.CuentaService;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de transferencia entre cuentas.
 */
public class TransferenciaView extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(TransferenciaView.class.getName());

    private final CuentaService cuentaService = new CuentaService();

    private JTextField campoCuentaOrigen;
    private JTextField campoCuentaDestino;
    private JTextField campoMonto;
    private final Tema.Banner banner = new Tema.Banner();

    public TransferenciaView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("EurekaBank · Transferencia");
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

        JLabel titulo = Tema.titulo("Realizar Transferencia");
        titulo.setBounds(44, 30, 700, 34);
        tarjeta.add(titulo);

        JLabel ayuda = Tema.ayuda("Transfiera fondos de una cuenta origen a una cuenta destino");
        ayuda.setBounds(44, 68, 800, 20);
        tarjeta.add(ayuda);

        banner.setBounds(44, 98, 912, 42);
        tarjeta.add(banner);

        JLabel lblOrigen = Tema.etiqueta("Cuenta origen");
        lblOrigen.setBounds(44, 152, 440, 16);
        tarjeta.add(lblOrigen);

        campoCuentaOrigen = new JTextField();
        Tema.estilizarCampo(campoCuentaOrigen);
        campoCuentaOrigen.setBounds(44, 172, 440, 44);
        tarjeta.add(campoCuentaOrigen);

        JLabel lblDestino = Tema.etiqueta("Cuenta destino");
        lblDestino.setBounds(516, 152, 440, 16);
        tarjeta.add(lblDestino);

        campoCuentaDestino = new JTextField();
        Tema.estilizarCampo(campoCuentaDestino);
        campoCuentaDestino.setBounds(516, 172, 440, 44);
        tarjeta.add(campoCuentaDestino);

        JLabel lblMonto = Tema.etiqueta("Monto a transferir");
        lblMonto.setBounds(44, 236, 400, 16);
        tarjeta.add(lblMonto);

        campoMonto = new JTextField();
        Tema.estilizarCampo(campoMonto);
        campoMonto.setBounds(44, 256, 912, 44);
        tarjeta.add(campoMonto);

        JButton botonTransferir = Tema.botonPrimario("Realizar Transferencia");
        botonTransferir.setBounds(44, 322, 912, 50);
        tarjeta.add(botonTransferir);

        JButton botonVolver = Tema.botonSecundario("Volver al Menú");
        botonVolver.setBounds(396, 400, 220, 44);
        tarjeta.add(botonVolver);

        JLabel nota = Tema.ayuda("Se validan las cuentas, el saldo disponible y que las cuentas sean distintas.");
        nota.setBounds(44, 470, 912, 20);
        tarjeta.add(nota);

        botonTransferir.addActionListener(e -> realizarTransferencia());
        campoMonto.addActionListener(e -> realizarTransferencia());
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

    private void realizarTransferencia() {
        banner.ocultar();

        String origen = limpiar(campoCuentaOrigen.getText());
        String destino = limpiar(campoCuentaDestino.getText());
        String montoStr = limpiar(campoMonto.getText());

        if (origen.isEmpty() || destino.isEmpty() || montoStr.isEmpty()) {
            banner.error("Complete cuenta origen, cuenta destino y monto.");
            return;
        }

        if (origen.equals(destino)) {
            banner.error("La cuenta origen y destino no pueden ser la misma.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException ex) {
            banner.error("El monto debe ser un número válido.");
            return;
        }

        if (monto <= 0) {
            banner.error("El monto debe ser mayor a cero.");
            return;
        }

        try {
            CuentaModel cOrigen = cuentaService.obtenerCuentaPorNumero(origen);
            if (cOrigen == null) {
                banner.error("No se encontró la cuenta origen: " + origen);
                return;
            }

            CuentaModel cDestino = cuentaService.obtenerCuentaPorNumero(destino);
            if (cDestino == null) {
                banner.error("No se encontró la cuenta destino: " + destino);
                return;
            }

            if (cOrigen.getDecCuenSaldo() < monto) {
                banner.error("Saldo insuficiente en cuenta origen. Disponible: $ "
                        + String.format("%.2f", cOrigen.getDecCuenSaldo()));
                return;
            }

            boolean exito = cuentaService.realizarTransferencia(origen, destino, montoStr);

            if (exito) {
                banner.exito("Transferencia realizada con éxito.");
                campoMonto.setText("");
            } else {
                banner.error("Error al realizar la transferencia. Verifique los datos o el saldo.");
            }
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al realizar la transferencia", e);
            banner.error("Error al realizar la transferencia: " + e.getMessage());
        }
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new TransferenciaView().setVisible(true));
    }
}
