package ec.edu.monster.vista;

import javax.swing.*;
import java.awt.*;

/**
 * Menú principal del cliente de escritorio.
 *
 * <p>Mismas cinco operaciones que la versión de referencia, presentadas como
 * tarjetas planas sobre la superficie clara de la identidad EurekaBank · GR8.</p>
 */
public class MenuView extends JFrame {

    private static final int ANCHO = 1280;
    private static final int ALTO = 720;

    private JPanel bgPanel;
    private JPanel headerPanel;
    private JPanel cardPanel;

    private JButton btnDeposito;
    private JButton btnRetiro;
    private JButton btnTransferencia;
    private JButton btnDatosCuenta;
    private JButton btnMovimientos;
    private JButton btnCerrarSesion;

    private JLabel lblUserName;

    private String username;

    public MenuView() {
        this("Usuario");
    }

    public MenuView(String username) {
        this.username = username;
        initComponents();
        configurarEventos();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setResizable(false);

        bgPanel = Tema.fondo(ANCHO, ALTO, "Menú principal");
        setContentPane(bgPanel);

        // Datos del usuario y salida en la barra superior
        headerPanel = new JPanel(null);
        headerPanel.setOpaque(false);
        headerPanel.setBounds(0, 0, ANCHO, Tema.ALTO_BANNER);
        bgPanel.add(headerPanel);

        lblUserName = new JLabel(username != null ? username.toUpperCase() : "MONSTER");
        lblUserName.setFont(Tema.fuente(Font.BOLD, 14));
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUserName.setBounds(ANCHO - 420, 26, 200, 22);
        headerPanel.add(lblUserName);

        JLabel lblRol = new JLabel("Sesión activa");
        lblRol.setFont(Tema.fuente(Font.PLAIN, 11));
        lblRol.setForeground(new Color(0xC7, 0xD2, 0xE0));
        lblRol.setHorizontalAlignment(SwingConstants.RIGHT);
        lblRol.setBounds(ANCHO - 420, 50, 200, 16);
        headerPanel.add(lblRol);

        btnCerrarSesion = Tema.botonSecundario("Salir");
        btnCerrarSesion.setBounds(ANCHO - 200, 30, 140, 36);
        btnCerrarSesion.setFont(Tema.fuente(Font.BOLD, 13));
        headerPanel.add(btnCerrarSesion);

        // TARJETA DE BIENVENIDA
        cardPanel = new Tema.Tarjeta();
        cardPanel.setBounds(70, 140, 1140, 480);
        bgPanel.add(cardPanel);

        JLabel lblTitulo = Tema.titulo("Bienvenido, "
                + (username != null ? username.toUpperCase() : "MONSTER"));
        lblTitulo.setBounds(48, 40, 700, 32);
        cardPanel.add(lblTitulo);

        JLabel lblSub = Tema.texto("Seleccione una operación para continuar.");
        lblSub.setForeground(Tema.TEXTO_SUAVE);
        lblSub.setBounds(48, 76, 700, 20);
        cardPanel.add(lblSub);

        JPanel panelBotones = new JPanel(new GridLayout(1, 5, 24, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBounds(48, 130, 1044, 200);
        cardPanel.add(panelBotones);

        btnDeposito = createMenuButton("Depósito", "Realizar un depósito a una cuenta");
        btnRetiro = createMenuButton("Retiro", "Realizar un retiro de una cuenta");
        btnTransferencia = createMenuButton("Transferencia", "Transferir dinero entre cuentas");
        btnDatosCuenta = createMenuButton("Datos de Cuenta", "Consultar información de una cuenta");
        btnMovimientos = createMenuButton("Movimientos", "Ver historial de movimientos");

        panelBotones.add(btnDeposito);
        panelBotones.add(btnRetiro);
        panelBotones.add(btnTransferencia);
        panelBotones.add(btnDatosCuenta);
        panelBotones.add(btnMovimientos);

        // Franja inferior de identidad (sin imágenes de mascotas)
        Tema.Tarjeta franja = new Tema.Tarjeta();
        franja.setRelleno(Tema.AZUL_MARINO);
        franja.setBounds(48, 356, 1044, 92);
        cardPanel.add(franja);

        JLabel logo = new JLabel(new Tema.Logo(48));
        logo.setBounds(28, 22, 48, 48);
        franja.add(logo);

        JLabel lblMarca = new JLabel(Tema.MARCA + " · Servicios web SOAP");
        lblMarca.setFont(Tema.fuente(Font.BOLD, 16));
        lblMarca.setForeground(Color.WHITE);
        lblMarca.setBounds(92, 26, 560, 22);
        franja.add(lblMarca);

        JLabel lblDetalle = new JLabel("Servidor WS_EurekaBank_Java_GR8 · Jakarta EE · MySQL");
        lblDetalle.setFont(Tema.fuente(Font.PLAIN, 12));
        lblDetalle.setForeground(new Color(0xC7, 0xD2, 0xE0));
        lblDetalle.setBounds(92, 50, 560, 18);
        franja.add(lblDetalle);

        JLabel lblGrupo = new JLabel(Tema.SUBTITULO_MARCA);
        lblGrupo.setFont(Tema.fuente(Font.BOLD, 12));
        lblGrupo.setForeground(Tema.AMBAR);
        lblGrupo.setHorizontalAlignment(SwingConstants.RIGHT);
        lblGrupo.setBounds(660, 40, 356, 18);
        franja.add(lblGrupo);
    }

    /**
     * Crea una tarjeta-botón plana de la nueva identidad.
     */
    private JButton createMenuButton(String titulo, String sub) {
        String html = "<html><div style='text-align:center;width:170px;'>"
                + "<span style='font-size:15px; font-weight:bold;'>" + titulo + "</span><br>"
                + "<span style='font-size:11px; font-weight:normal;'>" + sub + "</span>"
                + "</div></html>";

        JButton btn = new JButton(html) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Tema.RADIO, Tema.RADIO);
                g2.setColor(Tema.BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Tema.RADIO, Tema.RADIO);
                g2.setColor(Tema.TEAL);
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void setContentAreaFilled(boolean b) {
                // se mantiene el fondo personalizado
            }
        };

        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(Tema.AZUL_MARINO);
        btn.setBackground(Tema.SUPERFICIE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);
        return btn;
    }

    private void configurarEventos() {
        btnDeposito.addActionListener(evt -> {
            DepositoView depositoView = new DepositoView();
            depositoView.setLocationRelativeTo(null);
            depositoView.setVisible(true);
        });

        btnRetiro.addActionListener(evt -> {
            RetiroView retiroView = new RetiroView();
            retiroView.setLocationRelativeTo(null);
            retiroView.setVisible(true);
        });

        btnTransferencia.addActionListener(evt -> {
            TransferenciaView transferenciaView = new TransferenciaView();
            transferenciaView.setLocationRelativeTo(null);
            transferenciaView.setVisible(true);
        });

        btnMovimientos.addActionListener(evt -> {
            MovimientoView movimientoView = new MovimientoView();
            movimientoView.setLocationRelativeTo(null);
            movimientoView.setVisible(true);
        });

        btnDatosCuenta.addActionListener(evt -> {
            CuentaView cuentaView = new CuentaView();
            cuentaView.setLocationRelativeTo(null);
            cuentaView.setVisible(true);
        });

        btnCerrarSesion.addActionListener(evt -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginView login = new LoginView();
                login.setLocationRelativeTo(null);
                login.setVisible(true);
            });
        });
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

        EventQueue.invokeLater(() -> new MenuView().setVisible(true));
    }
}
