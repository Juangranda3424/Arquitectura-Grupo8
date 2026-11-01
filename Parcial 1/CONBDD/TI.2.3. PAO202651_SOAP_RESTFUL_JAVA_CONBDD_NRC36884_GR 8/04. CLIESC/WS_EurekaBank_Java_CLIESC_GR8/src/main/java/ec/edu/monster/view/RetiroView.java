package ec.edu.monster.view;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.service.CuentaService;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de retiro. Misma lógica del cliente original con la nueva identidad.
 */
public class RetiroView extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(RetiroView.class.getName());

    private final CuentaService cuentaService = new CuentaService();
    private CuentaModel cuentaActual = null;
    private String numeroCuentaActual = null;

    private JTextField campoNumeroCuenta;
    private JTextField campoMonto;
    private JLabel etiquetaCuenta;
    private JLabel etiquetaSaldo;
    private final Tema.Banner banner = new Tema.Banner();

    public RetiroView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("EurekaBank · Retiro");
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

        JLabel titulo = Tema.titulo("Realizar Retiro");
        titulo.setBounds(44, 30, 700, 34);
        tarjeta.add(titulo);

        JLabel ayuda = Tema.ayuda("Consulte la cuenta, verifique el saldo disponible e indique el monto");
        ayuda.setBounds(44, 68, 800, 20);
        tarjeta.add(ayuda);

        banner.setBounds(44, 98, 912, 42);
        tarjeta.add(banner);

        JLabel lblCuenta = Tema.etiqueta("Número de cuenta");
        lblCuenta.setBounds(44, 152, 400, 16);
        tarjeta.add(lblCuenta);

        campoNumeroCuenta = new JTextField();
        Tema.estilizarCampo(campoNumeroCuenta);
        campoNumeroCuenta.setBounds(44, 172, 912, 44);
        tarjeta.add(campoNumeroCuenta);

        JButton botonConsultar = Tema.botonPrimario("Consultar Cuenta");
        botonConsultar.setBounds(44, 228, 912, 46);
        tarjeta.add(botonConsultar);

        etiquetaCuenta = new JLabel("");
        etiquetaCuenta.setFont(Tema.fuente(Font.PLAIN, 12));
        etiquetaCuenta.setForeground(Tema.MUTED);
        etiquetaCuenta.setBounds(44, 284, 440, 18);
        tarjeta.add(etiquetaCuenta);

        etiquetaSaldo = new JLabel("");
        etiquetaSaldo.setFont(Tema.fuente(Font.BOLD, 15));
        etiquetaSaldo.setForeground(Tema.NAVY_2);
        etiquetaSaldo.setBounds(500, 282, 456, 22);
        tarjeta.add(etiquetaSaldo);

        JLabel lblMonto = Tema.etiqueta("Monto a retirar");
        lblMonto.setBounds(44, 316, 400, 16);
        tarjeta.add(lblMonto);

        campoMonto = new JTextField();
        Tema.estilizarCampo(campoMonto);
        campoMonto.setBounds(44, 336, 912, 44);
        tarjeta.add(campoMonto);

        JButton botonRetirar = Tema.botonPrimario("Realizar Retiro");
        botonRetirar.setBounds(44, 394, 912, 50);
        tarjeta.add(botonRetirar);

        JButton botonVolver = Tema.botonSecundario("Volver al Menú");
        botonVolver.setBounds(396, 462, 220, 44);
        tarjeta.add(botonVolver);

        botonConsultar.addActionListener(e -> buscarCuenta());
        campoNumeroCuenta.addActionListener(e -> buscarCuenta());
        botonRetirar.addActionListener(e -> realizarRetiro());
        campoMonto.addActionListener(e -> realizarRetiro());
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
        banner.ocultar();
        String numeroCuenta = limpiar(campoNumeroCuenta.getText());

        if (numeroCuenta.isEmpty()) {
            banner.error("Por favor, ingrese un número de cuenta.");
            limpiarEstado();
            return;
        }

        try {
            CuentaModel cuentaModel = cuentaService.obtenerCuentaPorNumero(numeroCuenta);

            if (cuentaModel == null) {
                banner.error("No se encontraron datos para la cuenta: " + numeroCuenta);
                limpiarEstado();
                return;
            }

            cuentaActual = cuentaModel;
            numeroCuentaActual = numeroCuenta;

            etiquetaCuenta.setText("Cuenta: " + numeroCuenta);
            etiquetaSaldo.setText("Saldo disponible: $ " + String.format("%.2f", cuentaModel.getDecCuenSaldo()));

            campoMonto.setText("");
            banner.exito("Cuenta encontrada. Ingrese el monto a retirar.");
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al obtener los datos de la cuenta", ex);
            banner.error("Error al obtener los datos de la cuenta: " + ex.getMessage());
            limpiarEstado();
        }
    }

    private void realizarRetiro() {
        banner.ocultar();

        if (cuentaActual == null || numeroCuentaActual == null) {
            banner.error("Primero debe consultar una cuenta válida.");
            return;
        }

        String montoStr = limpiar(campoMonto.getText());
        if (montoStr.isEmpty()) {
            banner.error("Por favor, ingrese un monto a retirar.");
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr);
            if (monto <= 0) {
                banner.error("El monto debe ser mayor a cero.");
                return;
            }

            CuentaModel cuentaActualizada = cuentaService.obtenerCuentaPorNumero(numeroCuentaActual);
            if (cuentaActualizada == null) {
                banner.error("No se pudo obtener la información actualizada de la cuenta.");
                return;
            }
            cuentaActual = cuentaActualizada;

            double saldoActual = cuentaActual.getDecCuenSaldo();
            etiquetaSaldo.setText("Saldo disponible: $ " + String.format("%.2f", saldoActual));

            if (saldoActual < monto) {
                banner.error("Saldo insuficiente. Disponible: $ " + String.format("%.2f", saldoActual)
                        + " | Solicitado: $ " + String.format("%.2f", monto));
                return;
            }

            boolean exito = cuentaService.realizarRetiro(numeroCuentaActual, montoStr);

            if (exito) {
                banner.exito("Retiro realizado con éxito.");
                CuentaModel despues = cuentaService.obtenerCuentaPorNumero(numeroCuentaActual);
                if (despues != null) {
                    cuentaActual = despues;
                    etiquetaSaldo.setText("Saldo disponible: $ " + String.format("%.2f", despues.getDecCuenSaldo()));
                }
                campoMonto.setText("");
            } else {
                banner.error("Error al realizar el retiro. Verifique que tenga fondos suficientes.");
            }
        } catch (NumberFormatException e) {
            banner.error("Por favor, ingrese un monto válido (número).");
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al realizar el retiro", e);
            banner.error("Error al realizar el retiro: " + e.getMessage());
        }
    }

    private void limpiarEstado() {
        cuentaActual = null;
        numeroCuentaActual = null;
        etiquetaCuenta.setText("");
        etiquetaSaldo.setText("");
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new RetiroView().setVisible(true));
    }
}
