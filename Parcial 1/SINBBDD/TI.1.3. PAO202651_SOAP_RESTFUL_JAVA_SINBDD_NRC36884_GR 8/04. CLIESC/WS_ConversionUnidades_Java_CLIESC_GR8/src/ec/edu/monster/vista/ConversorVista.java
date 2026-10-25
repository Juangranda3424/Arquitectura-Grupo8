package ec.edu.monster.vista;

import ec.edu.monster.controlador.Autenticador;
import ec.edu.monster.controlador.ConversorUnidades;
import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.servicio.ClienteREST;
import ec.edu.monster.servicio.RespuestaAPI;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ventana Swing del conversor de unidades sobre el servicio REST.
 *
 * <p>Conserva la estructura y el flujo de TI.1.1 (combos de magnitud/origen/destino, valor de
 * entrada, resultado y botones Convertir / Cerrar Sesión). Los combos de unidades se cargan
 * dinámicamente desde {@code GET /api/catalogo} tras el login, con etiquetas
 * {@code CLAVE (Etiqueta, símbolo)} y catálogo local de respaldo si el servidor no responde.</p>
 */
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
    private final ClienteREST clienteREST;
    private final String tokenUsuario;

    /** Catálogo vigente: magnitud -> unidades publicadas por el servidor (o respaldo local). */
    private Map<String, List<UnidadItem>> catalogo;

    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_PELIGRO = new Color(220, 38, 38);
    private static final Color COLOR_TEXTO = new Color(30, 41, 59);

    /** Entrada del catálogo: clave (nombre de la unidad en la API), etiqueta y símbolo. */
    private record UnidadItem(String clave, String etiqueta, String simbolo) {

        /** Texto del combo: {@code CLAVE (Etiqueta, símbolo)}. */
        String etiquetaMenu() {
            return Constantes.formatearUnidad(clave, etiqueta, simbolo);
        }
    }

    /** Catálogo local de último recurso: solo claves, sin etiquetas ni símbolos. */
    private static final String[] RESPALDO_LONGITUD = {
        "CENTIMETRO", "METRO", "PIE", "YARDA", "MILLA"
    };
    private static final String[] RESPALDO_MASA = {
        "GRAMO", "KILOGRAMO", "ONZA", "LIBRA", "QUINTAL"
    };
    private static final String[] RESPALDO_TEMPERATURA = {
        "CELSIUS", "FAHRENHEIT", "KELVIN", "NEWTON", "REAUMUR"
    };

    public ConversorVista(String tokenUsuario) {
        this.tokenUsuario = tokenUsuario == null ? "" : tokenUsuario;
        if (!this.tokenUsuario.isBlank()) {
            Constantes.token = this.tokenUsuario;
        }

        // Un único ClienteREST compartido entre ambos controladores
        this.clienteREST = new ClienteREST();
        this.controlador = new ConversorUnidades(clienteREST);
        this.autenticador = new Autenticador(clienteREST);

        setTitle("Conversor de Unidades - REST");
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

        cbMagnitud = new JComboBox<>();
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

        // Listeners
        cbMagnitud.addActionListener(e -> actualizarCombosUnidades((String) cbMagnitud.getSelectedItem()));
        btnConvertir.addActionListener(e -> realizarConversionAsync());
        btnCerrarSesion.addActionListener(e -> realizarLogoutAsync());

        // Catálogo desde el servidor (asíncrono para no congelar la interfaz)
        cargarCatalogoAsync();
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

    // ------------------------------------------------------------------
    // Catálogo (GET /api/catalogo)
    // ------------------------------------------------------------------

    private void cargarCatalogoAsync() {
        toggleCarga(true);

        SwingWorker<Map<String, List<UnidadItem>>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, List<UnidadItem>> doInBackground() throws Exception {
                RespuestaAPI respuesta = clienteREST.catalogo();
                if (!respuesta.esExitosa()) {
                    throw new Exception(respuesta.getMensaje());
                }
                JsonObject datos = respuesta.getResultado();
                if (datos == null || datos.isEmpty()) {
                    throw new Exception("El servidor no devolvió ninguna unidad.");
                }
                Map<String, List<UnidadItem>> desdeServidor = parsearCatalogo(datos);
                if (desdeServidor.isEmpty()) {
                    throw new Exception("El catálogo recibido está vacío.");
                }
                return desdeServidor;
            }

            @Override
            protected void done() {
                try {
                    catalogo = get();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ConversorVista.this,
                            "No se pudo cargar el catálogo de unidades.\n"
                                    + mensajeError(ex)
                                    + "\nSe usará el catálogo local de respaldo.",
                            "Error REST", JOptionPane.ERROR_MESSAGE);
                    catalogo = catalogoRespaldo();
                } finally {
                    refrescarMagnitudes();
                    toggleCarga(false);
                }
            }
        };

        worker.execute();
    }

    private Map<String, List<UnidadItem>> parsearCatalogo(JsonObject datos) {
        Map<String, List<UnidadItem>> resultado = new LinkedHashMap<>();

        for (Map.Entry<String, JsonValue> magnitud : datos.entrySet()) {
            if (magnitud.getValue().getValueType() != JsonValue.ValueType.ARRAY) {
                continue;
            }
            JsonArray arreglo = magnitud.getValue().asJsonArray();
            List<UnidadItem> unidades = new ArrayList<>();
            for (JsonValue valor : arreglo) {
                if (valor.getValueType() != JsonValue.ValueType.OBJECT) {
                    continue;
                }
                JsonObject unidad = valor.asJsonObject();
                unidades.add(new UnidadItem(
                        unidad.getString("clave", ""),
                        unidad.getString("etiqueta", ""),
                        unidad.getString("simbolo", "")));
            }
            if (!unidades.isEmpty()) {
                resultado.put(magnitud.getKey(), unidades);
            }
        }

        return resultado;
    }

    private Map<String, List<UnidadItem>> catalogoRespaldo() {
        Map<String, List<UnidadItem>> resultado = new LinkedHashMap<>();
        resultado.put(Constantes.LONGITUD, respaldo(RESPALDO_LONGITUD));
        resultado.put(Constantes.MASA, respaldo(RESPALDO_MASA));
        resultado.put(Constantes.TEMPERATURA, respaldo(RESPALDO_TEMPERATURA));
        return resultado;
    }

    private List<UnidadItem> respaldo(String[] claves) {
        List<UnidadItem> unidades = new ArrayList<>();
        for (String clave : claves) {
            unidades.add(new UnidadItem(clave, "", ""));
        }
        return unidades;
    }

    private void refrescarMagnitudes() {
        cbMagnitud.removeAllItems();
        if (catalogo != null) {
            for (String magnitud : catalogo.keySet()) {
                cbMagnitud.addItem(magnitud);
            }
        }
        actualizarCombosUnidades((String) cbMagnitud.getSelectedItem());
    }

    private void actualizarCombosUnidades(String magnitud) {
        cbOrigen.removeAllItems();
        cbDestino.removeAllItems();

        if (magnitud == null || catalogo == null) {
            return;
        }

        List<UnidadItem> unidades = catalogo.get(magnitud);
        if (unidades == null) {
            return;
        }

        for (UnidadItem u : unidades) {
            cbOrigen.addItem(u.etiquetaMenu());
            cbDestino.addItem(u.etiquetaMenu());
        }
    }

    /** Traduce la etiqueta del combo a la clave de la API para la magnitud activa. */
    private String claveDe(String magnitud, String etiquetaMenu) {
        if (magnitud == null || etiquetaMenu == null || catalogo == null) {
            return etiquetaMenu;
        }
        List<UnidadItem> unidades = catalogo.get(magnitud);
        if (unidades != null) {
            for (UnidadItem u : unidades) {
                if (u.etiquetaMenu().equals(etiquetaMenu)) {
                    return u.clave();
                }
            }
        }
        return etiquetaMenu;
    }

    // ------------------------------------------------------------------
    // Conversión
    // ------------------------------------------------------------------

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
        String origenSeleccionado = (String) cbOrigen.getSelectedItem();
        String destinoSeleccionado = (String) cbDestino.getSelectedItem();

        if (magnitud == null || origenSeleccionado == null || destinoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona la magnitud y las unidades.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String origen = claveDe(magnitud, origenSeleccionado);
        String destino = claveDe(magnitud, destinoSeleccionado);

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
                            mensajeError(ex),
                            "Error REST", JOptionPane.ERROR_MESSAGE);
                } finally {
                    toggleCarga(false);
                }
            }
        };

        worker.execute();
    }

    // ------------------------------------------------------------------
    // Logout
    // ------------------------------------------------------------------

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
                return autenticador.logout();
            }

            @Override
            protected void done() {
                try {
                    boolean exito = get();
                    if (exito) {
                        JOptionPane.showMessageDialog(ConversorVista.this,
                                "Sesión cerrada correctamente.", "Logout",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        // Vuelve a abrir una ventana de Login limpia (TI.1.1 lo dejó comentado)
                        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
                    } else {
                        JOptionPane.showMessageDialog(ConversorVista.this,
                                "No se pudo cerrar la sesión en el servidor.",
                                "Error REST", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ConversorVista.this,
                            mensajeError(ex),
                            "Error REST", JOptionPane.ERROR_MESSAGE);
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

    /**
     * Devuelve el mensaje más útil disponible de una excepción de {@link SwingWorker#get()}.
     *
     * <p>Evita el NPE de TI.1.1 ({@code ex.getCause().getMessage()}): si no hay causa o su mensaje
     * es nulo, cae a {@code ex.getMessage()} y, en último caso, a un texto genérico.</p>
     */
    private static String mensajeError(Throwable ex) {
        Throwable causa = ex.getCause();
        if (causa != null && causa.getMessage() != null && !causa.getMessage().isBlank()) {
            return causa.getMessage();
        }
        if (ex.getMessage() != null && !ex.getMessage().isBlank()) {
            return ex.getMessage();
        }
        return "Ocurrió un error inesperado.";
    }
}
