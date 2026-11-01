package ec.edu.monster.vista;

import ec.edu.monster.controlador.MainController;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de inicio de sesión del cliente de escritorio.
 *
 * <p>Identidad EurekaBank · Grupo 8: barra azul marino, tarjeta de acceso con
 * campos de usuario y contraseña, botón plano teal y panel lateral con el
 * monograma vectorial.</p>
 */
public class LoginView extends JFrame {

    private JPanel panelBackground;
    private Tema.Tarjeta panelCard;
    private JPanel panelMarca;
    private Tema.Mensaje mensaje;

    private JTextField lblUsername;
    private JPasswordField lblPassword;
    private JButton btnLogin;
    private JLabel messageLabel;

    private final MainController mainController = new MainController();
    private String usuarioActual;

    public LoginView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        // FONDO con barra superior de la identidad
        panelBackground = Tema.fondo(1100, 650, "Acceso");
        getContentPane().add(panelBackground);

        // TARJETA DE ACCESO
        panelCard = new Tema.Tarjeta();
        panelCard.setBounds(80, 150, 580, 440);
        panelBackground.add(panelCard);

        JLabel lblTitulo = Tema.titulo("Iniciar sesión");
        lblTitulo.setBounds(48, 40, 480, 34);
        panelCard.add(lblTitulo);

        JLabel lblSubtitulo = Tema.texto("Ingrese sus credenciales para operar en EurekaBank.");
        lblSubtitulo.setForeground(Tema.TEXTO_SUAVE);
        lblSubtitulo.setBounds(48, 78, 480, 20);
        panelCard.add(lblSubtitulo);

        JLabel lblUserText = Tema.etiqueta("USUARIO");
        lblUserText.setBounds(48, 128, 480, 16);
        panelCard.add(lblUserText);

        lblUsername = Tema.campo();
        lblUsername.setBounds(48, 148, 480, 44);
        panelCard.add(lblUsername);

        JLabel lblPassText = Tema.etiqueta("CONTRASEÑA");
        lblPassText.setBounds(48, 208, 480, 16);
        panelCard.add(lblPassText);

        lblPassword = Tema.campoClave();
        lblPassword.setBounds(48, 228, 480, 44);
        panelCard.add(lblPassword);

        btnLogin = Tema.botonPrimario("Entrar");
        btnLogin.setBounds(48, 296, 480, 46);
        panelCard.add(btnLogin);

        mensaje = new Tema.Mensaje();
        mensaje.setBounds(48, 356, 480, 40);
        panelCard.add(mensaje);

        messageLabel = new JLabel("", SwingConstants.LEFT);
        messageLabel.setFont(Tema.fuente(Font.PLAIN, 12));
        messageLabel.setForeground(Tema.TEXTO_SUAVE);
        messageLabel.setText("Autenticación validada por el servicio WSLogin del servidor SOAP.");
        messageLabel.setBounds(48, 400, 480, 18);
        panelCard.add(messageLabel);

        // PANEL LATERAL con el monograma vectorial
        panelMarca = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.AZUL_MARINO_2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Tema.RADIO, Tema.RADIO);
                g2.setColor(Tema.AMBAR);
                g2.fillRoundRect(28, 40, 54, 6, 6, 6);
                g2.setColor(new Color(0x1C, 0x40, 0x74));
                g2.fillRoundRect(28, 300, getWidth() - 56, 90, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelMarca.setOpaque(false);
        panelMarca.setBounds(700, 150, 320, 440);
        panelBackground.add(panelMarca);

        JLabel logo = new JLabel(new Tema.Logo(96));
        logo.setBounds(28, 70, 96, 96);
        panelMarca.add(logo);

        JLabel lblMarca = new JLabel(Tema.MARCA);
        lblMarca.setFont(Tema.fuente(Font.BOLD, 26));
        lblMarca.setForeground(Color.WHITE);
        lblMarca.setBounds(28, 186, 270, 30);
        panelMarca.add(lblMarca);

        JLabel lblSub = new JLabel(Tema.SUBTITULO_MARCA);
        lblSub.setFont(Tema.fuente(Font.PLAIN, 13));
        lblSub.setForeground(new Color(0xC7, 0xD2, 0xE0));
        lblSub.setBounds(28, 216, 270, 20);
        panelMarca.add(lblSub);

        JLabel lblPie = new JLabel("Banca por internet");
        lblPie.setFont(Tema.fuente(Font.BOLD, 14));
        lblPie.setForeground(Tema.AMBAR);
        lblPie.setBounds(28, 260, 270, 20);
        panelMarca.add(lblPie);

        JLabel lblDetalle = new JLabel("Depósitos · Retiros · Transferencias");
        lblDetalle.setFont(Tema.fuente(Font.PLAIN, 12));
        lblDetalle.setForeground(new Color(0xC7, 0xD2, 0xE0));
        lblDetalle.setBounds(28, 282, 270, 18);
        panelMarca.add(lblDetalle);

        JLabel lblServicios = new JLabel("WSLogin · WSCuenta · WSMovimiento");
        lblServicios.setFont(Tema.fuente(Font.PLAIN, 11));
        lblServicios.setForeground(new Color(0x9F, 0xB0, 0xC4));
        lblServicios.setBounds(28, 366, 270, 18);
        panelMarca.add(lblServicios);

        // Acción del botón con MainController (misma lógica)
        btnLogin.addActionListener(e -> {
            String username = lblUsername.getText();
            String passwordString = new String(lblPassword.getPassword());
            if (username.isEmpty() || passwordString.isEmpty()) {
                mensaje.error("Ingrese usuario y contraseña.");
                return;
            }
            mensaje.limpiar();
            boolean ok = mainController.iniciarSesion(username, passwordString);
            if (ok) {
                usuarioActual = username;
                this.setVisible(false);
                java.awt.EventQueue.invokeLater(() -> {
                    MenuView mv = new MenuView(usuarioActual);
                    mv.setLocationRelativeTo(null);
                    mv.setVisible(true);
                    this.dispose();
                });
            } else {
                mensaje.error("Credenciales inválidas.");
            }
        });

        setContentPane(panelBackground);
    }

    // Getters para compatibilidad
    public JTextField getTxtUsername() {
        return lblUsername;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JLabel getMessageLabel() {
        return messageLabel;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }

        EventQueue.invokeLater(() -> new LoginView().setVisible(true));
    }
}
