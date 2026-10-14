package ec.edu.monster.vista;

import ec.edu.monster.controlador.Autenticador;
import ec.edu.monster.controlador.ConversorUnidades;
import ec.edu.monster.modelo.Constantes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConversorVista extends JFrame {

    private final JComboBox<String> cbMagnitud;
    private final JComboBox<String> cbOrigen;
    private final JComboBox<String> cbDestino;
    private final JTextField txtValorOrigen;
    private final JTextField txtResultado;
    private final JButton btnConvertir;
    private final JButton btnCerrarSesion;
    private final JProgressBar progressBar;

    private final ConversorUnidades controlador;
    private final Autenticador autenticador;
    private final String tokenUsuario;

    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_PELIGRO = new Color(220, 38, 38);
    private static final Color COLOR_TEXTO = new Color(30, 41, 59);

    public ConversorVista(String tokenUsuario) {
        this.tokenUsuario = tokenUsuario;
        this.controlador = new ConversorUnidades();
        this.autenticador = new Autenticador();

        setTitle("Conversor de Unidades - SOAP");
        setSize(500, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Conversor de Unidades", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitulo, gbc);

        // Seleccionar Magnitud
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(crearLabel("Tipo de Magnitud:"), gbc);

        cbMagnitud = new JComboBox<>(Constantes.MAGNITUD);
        gbc.gridx = 1;
        mainPanel.add(cbMagnitud, gbc);

        // Unidad Origen
        gbc.gridy = 2; gbc.gridx = 0;
        mainPanel.add(crearLabel("Unidad Origen:"), gbc);

        cbOrigen = new JComboBox<>();
        gbc.gridx = 1;
        mainPanel.add(cbOrigen, gbc);

        // Unidad Destino
        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(crearLabel("Unidad Destino:"), gbc);

        cbDestino = new JComboBox<>();
        gbc.gridx = 1;
        mainPanel.add(cbDestino, gbc);

        // Valor a Convertir
        gbc.gridy = 4; gbc.gridx = 0;
        mainPanel.add(crearLabel("Valor Origen:"), gbc);

        txtValorOrigen = new JTextField();
        estilarCampo(txtValorOrigen);
        gbc.gridx = 1;
        mainPanel.add(txtValorOrigen, gbc);

        // Resultado
        gbc.gridy = 5; gbc.gridx = 0;
        mainPanel.add(crearLabel("Resultado:"), gbc);

        txtResultado = new JTextField();
        estilarCampo(txtResultado);
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtResultado.setBackground(new Color(241, 245, 249));
        gbc.gridx = 1;
        mainPanel.add(txtResultado, gbc);

        // Barra de progreso
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(progressBar, gbc);

        // Botón Convertir
        btnConvertir = new JButton("Convertir");
        estilarBoton(btnConvertir, COLOR_PRIMARIO);
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(btnConvertir, gbc);

        // Botón Cerrar Sesión
        btnCerrarSesion = new JButton("Cerrar Sesión");
        estilarBoton(btnCerrarSesion, COLOR_PELIGRO);
        gbc.gridy = 8;
        mainPanel.add(btnCerrarSesion, gbc);

        add(mainPanel);

        // Cargar combos por primera vez
        actualizarCombosUnidades((String) cbMagnitud.getSelectedItem());

        // Listeners
        cbMagnitud.addActionListener(e -> actualizarCombosUnidades((String) cbMagnitud.getSelectedItem()));
        btnConvertir.addActionListener(e -> realizarConversionAsync());
        btnCerrarSesion.addActionListener(e -> realizarLogoutAsync());
    }

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_TEXTO);
        return label;
    }

    private void estilarCampo(JTextField field) {
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 34));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private void estilarBoton(JButton btn, Color colorFondo) {
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 38));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void actualizarCombosUnidades(String magnitud) {
        cbOrigen.removeAllItems();
        cbDestino.removeAllItems();

        String[] unidades = switch (magnitud) {
            case "LONGITUD" -> Constantes.LONGITUD;
            case "MASA" -> Constantes.MASA;
            case "TEMPERATURA" -> Constantes.TEMPERATURA;
            default -> new String[]{};
        };

        for (String u : unidades) {
            cbOrigen.addItem(u);
            cbDestino.addItem(u);
        }
    }

    private void realizarConversionAsync() {
        String textoValor = txtValorOrigen.getText().trim();
        if (textoValor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un valor numérico.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(textoValor);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String magnitud = (String) cbMagnitud.getSelectedItem();
        String origen = (String) cbOrigen.getSelectedItem();
        String destino = (String) cbDestino.getSelectedItem();

        toggleCarga(true);

        SwingWorker<Double, Void> worker = new SwingWorker<>() {
            @Override
            protected Double doInBackground() throws Exception {
                return controlador.ejecutarConversion(valor, origen, destino, magnitud);
            }

            @Override
            protected void done() {
                try {
                    Double resultado = get();
                    txtResultado.setText(String.format("%.4f", resultado));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ConversorVista.this,
                            "Error al realizar la conversión:\n" + ex.getMessage(),
                            "Error SOAP", JOptionPane.ERROR_MESSAGE);
                } finally {
                    toggleCarga(false);
                }
            }
        };

        worker.execute();
    }

    private void realizarLogoutAsync() {
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas cerrar sesión?",
                "Confirmar Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        toggleCarga(true);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return autenticador.logout(tokenUsuario);
            }

            @Override
            protected void done() {
                try {
                    boolean exito = get();
                    if (exito) {
                        JOptionPane.showMessageDialog(ConversorVista.this, "Sesión cerrada correctamente.", "Logout", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Cierra la ventana actual
                        
                        // Abre la ventana de Login si la tienes implementada
                        // new LoginVista().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(ConversorVista.this, "No se pudo cerrar la sesión en el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ConversorVista.this,
                            "Error al cerrar sesión:\n" + ex.getMessage(),
                            "Error SOAP", JOptionPane.ERROR_MESSAGE);
                } finally {
                    toggleCarga(false);
                }
            }
        };

        worker.execute();
    }

    private void toggleCarga(boolean cargando) {
        btnConvertir.setEnabled(!cargando);
        btnCerrarSesion.setEnabled(!cargando);
        progressBar.setVisible(cargando);
    }
}