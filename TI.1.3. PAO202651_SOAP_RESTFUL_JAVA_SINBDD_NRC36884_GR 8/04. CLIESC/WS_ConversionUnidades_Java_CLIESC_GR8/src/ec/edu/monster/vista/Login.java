package ec.edu.monster.vista;

import ec.edu.monster.controlador.Autenticador;
import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Login extends JFrame {

    private final JTextField txtUsuario;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;
    private final JProgressBar progressBar;

    private final Autenticador autenticador;

    // Paleta de colores moderna
    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);     // Azul moderno
    private static final Color COLOR_HOVER = new Color(29, 78, 216);        // Azul oscuro al pasar cursor
    private static final Color COLOR_TEXTO = new Color(30, 41, 59);         // Gris oscuro para legibilidad
    private static final Color COLOR_SUBTITULO = new Color(100, 116, 139);   // Gris neutro

    public Login() {
        autenticador = new Autenticador();

        setTitle("Inicio de Sesión - REST");
        setSize(420, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel Principal con margen padding uniforme
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- ENCABEZADO ---
        JLabel lblTitulo = new JLabel("Bienvenido", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_TEXTO);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitulo, gbc);

        JLabel lblSubtitulo = new JLabel("Ingresa tus credenciales para continuar", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(COLOR_SUBTITULO);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 4, 20, 4);
        mainPanel.add(lblSubtitulo, gbc);

        // Reset de insets para el formulario
        gbc.insets = new Insets(6, 4, 6, 4);

        // --- CAMPO USUARIO ---
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsuario.setForeground(COLOR_TEXTO);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(lblUsuario, gbc);

        txtUsuario = new JTextField();
        estilarCampo(txtUsuario);
        gbc.gridy = 3;
        mainPanel.add(txtUsuario, gbc);

        // --- CAMPO CONTRASEÑA ---
        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(COLOR_TEXTO);
        gbc.gridy = 4;
        mainPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        estilarCampo(txtPassword);
        gbc.gridy = 5;
        mainPanel.add(txtPassword, gbc);

        // --- INDICADOR DE CARGA (BARRA DE PROGRESO) ---
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(progressBar.getPreferredSize().width, 4));
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 4, 5, 4);
        mainPanel.add(progressBar, gbc);

        // --- BOTÓN LOGIN ---
        btnLogin = new JButton("Iniciar sesión");
        estilarBoton(btnLogin);
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 4, 5, 4);
        mainPanel.add(btnLogin, gbc);

        add(mainPanel);

        // Listeners
        btnLogin.addActionListener(e -> ejecutarInicioSesionAsync());
        getRootPane().setDefaultButton(btnLogin);
    }

    private void estilarCampo(JTextField field) {
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 36));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Ajuste de borde moderno
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void estilarBoton(JButton btn) {
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(COLOR_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto Hover simple
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(COLOR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(COLOR_PRIMARIO);
            }
        });
    }

    private void cambiarEstadoCarga(boolean cargando) {
        txtUsuario.setEnabled(!cargando);
        txtPassword.setEnabled(!cargando);
        btnLogin.setEnabled(!cargando);
        progressBar.setVisible(cargando);
        btnLogin.setText(cargando ? "Conectando..." : "Iniciar sesión");
    }

    private void ejecutarInicioSesionAsync() {
        String username = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, completa todos los campos requeridos.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        cambiarEstadoCarga(true);

        // SwingWorker evita congelar la interfaz al llamar al servicio REST
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                Usuario usuario = new Usuario(username, password);
                return autenticador.login(usuario);
            }

            @Override
            protected void done() {
                try {
                    String respuesta = get();
                    procesarRespuesta(respuesta);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            Login.this,
                            "No se pudo conectar con el servicio REST.\n" + mensajeError(ex),
                            "Error de Conexión",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    cambiarEstadoCarga(false);
                }
            }
        };

        worker.execute();
    }

    private void procesarRespuesta(String respuesta) {
        if (respuesta != null && !respuesta.isBlank()
                && !respuesta.toLowerCase().contains("error")
                && !respuesta.toLowerCase().contains("inválido")
                && !respuesta.toLowerCase().contains("invalido")) {

            // Se asigna la respuesta (token)
            Constantes.token = respuesta;

            JOptionPane.showMessageDialog(
                    this,
                    "Sesión iniciada con éxito.",
                    "Bienvenido",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Abrir la ventana principal de conversión y cerrar el Login
            new ConversorVista(Constantes.token).setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    respuesta == null ? "Sin respuesta del servidor." : respuesta,
                    "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Devuelve el mensaje más útil disponible de una excepción de {@link SwingWorker#get()}.
     *
     * <p>Corrige el NPE de TI.1.1 ({@code ex.getCause().getMessage()}): si no hay causa o su
     * mensaje es nulo, cae a {@code ex.getMessage()} y, en último caso, a un texto genérico.</p>
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

    // Método main con Look & Feel moderno
    public static void main(String[] args) {
        try {
            // Se recomienda agregar la librería FlatLaf si usas Maven/Gradle:
            // com.formdev.flatlaf.FlatLightLaf.setup();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
