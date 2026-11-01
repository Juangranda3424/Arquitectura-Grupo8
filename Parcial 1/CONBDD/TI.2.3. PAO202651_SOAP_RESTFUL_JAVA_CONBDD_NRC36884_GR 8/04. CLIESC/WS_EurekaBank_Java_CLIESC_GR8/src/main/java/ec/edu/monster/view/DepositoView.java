package ec.edu.monster.view;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.service.CuentaService;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de depósito. Misma lógica del cliente original (consultar cuenta y
 * registrar el depósito contra el servicio RESTful) con la nueva identidad.
 */
public class DepositoView extends JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(DepositoView.class.getName());

    private final CuentaService cuentaService = new CuentaService();
    private CuentaModel cuentaActual = null;
    private String numeroCuentaActual = null;

    private JTextField campoNumeroCuenta;
    private JTextField campoMonto;
    private final Tema.Banner banner = new Tema.Banner();

    public DepositoView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("EurekaBank · Depósito");
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

        JLabel titulo = Tema.titulo("Realizar Depósito");
        titulo.setBounds(44, 30, 700, 34);
        tarjeta.add(titulo);

        JLabel ayuda = Tema.ayuda("Consulte la cuenta y luego indique el monto a depositar");
        ayuda.setBounds(44, 68, 760, 20);
        tarjeta.add(ayuda);

        banner.setBounds(44, 98, 912, 42);
        tarjeta.add(banner);

        JLabel lblCuenta = Tema.etiqueta("Número de cuenta");
        lblCuenta.setBounds(44, 158, 400, 16);
        tarjeta.add(lblCuenta);

        campoNumeroCuenta = new JTextField();
        Tema.estilizarCampo(campoNumeroCuenta);
        campoNumeroCuenta.setBounds(44, 178, 912, 44);
        tarjeta.add(campoNumeroCuenta);

        JButton botonConsultar = Tema.botonPrimario("Consultar Cuenta");
        botonConsultar.setBounds(44, 234, 912, 46);
        tarjeta.add(botonConsultar);

        JLabel lblMonto = Tema.etiqueta("Monto a depositar");
        lblMonto.setBounds(44, 296, 400, 16);
        tarjeta.add(lblMonto);

        campoMonto = new JTextField();
        Tema.estilizarCampo(campoMonto);
        campoMonto.setBounds(44, 316, 912, 44);
        tarjeta.add(campoMonto);

        JButton botonDepositar = Tema.botonPrimario("Realizar Depósito");
        botonDepositar.setBounds(44, 374, 912, 50);
        tarjeta.add(botonDepositar);

        JButton botonVolver = Tema.botonSecundario("Volver al Menú");
        botonVolver.setBounds(396, 450, 220, 44);
        tarjeta.add(botonVolver);

        botonConsultar.addActionListener(e -> buscarCuenta());
        campoNumeroCuenta.addActionListener(e -> buscarCuenta());
        botonDepositar.addActionListener(e -> realizarDeposito());
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
            cuentaActual = null;
            numeroCuentaActual = null;
            return;
        }

        try {
            CuentaModel cuentaModel = cuentaService.obtenerCuentaPorNumero(numeroCuenta);

            if (cuentaModel == null) {
                banner.error("No se encontraron datos para la cuenta: " + numeroCuenta);
                cuentaActual = null;
                numeroCuentaActual = null;
                return;
            }

            cuentaActual = cuentaModel;
            numeroCuentaActual = numeroCuenta;
            banner.exito("Cuenta " + numeroCuenta + " encontrada. Ahora ingrese el monto a depositar.");
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error al obtener los datos de la cuenta", ex);
            banner.error("Error al obtener los datos de la cuenta: " + ex.getMessage());
            cuentaActual = null;
            numeroCuentaActual = null;
        }
    }

    private void realizarDeposito() {
        banner.ocultar();

        if (cuentaActual == null || numeroCuentaActual == null) {
            banner.error("Primero debe consultar una cuenta válida.");
            return;
        }

        String montoStr = limpiar(campoMonto.getText());
        if (montoStr.isEmpty()) {
            banner.error("Por favor, ingrese un monto a depositar.");
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr);
            if (monto <= 0) {
                banner.error("El monto debe ser mayor a cero.");
                return;
            }

            boolean exito = cuentaService.realizarDeposito(numeroCuentaActual, montoStr, "DEP", null);

            if (exito) {
                banner.exito("Depósito realizado con éxito.");
                CuentaModel actualizada = cuentaService.obtenerCuentaPorNumero(numeroCuentaActual);
                if (actualizada != null) {
                    cuentaActual = actualizada;
                }
                campoMonto.setText("");
            } else {
                banner.error("Error al realizar el depósito.");
            }
        } catch (NumberFormatException e) {
            banner.error("Por favor, ingrese un monto válido (número).");
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al realizar el depósito", e);
            banner.error("Error al realizar el depósito: " + e.getMessage());
        }
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new DepositoView().setVisible(true));
    }
}
