package ec.edu.monster.view;

import ec.edu.monster.controller.LoginController;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de inicio de sesión (identidad EurekaBank · GR8).
 */
public class LoginView extends JFrame {

    private JTextField campoUsuario;
    private JPasswordField campoClave;
    private JButton botonLogin;
    private JLabel etiquetaMensaje;

    public LoginView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("EurekaBank · Iniciar Sesión");
        setSize(1100, 680);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel fondo = new JPanel(null);
        fondo.setBackground(Tema.FONDO);
        fondo.setBounds(0, 0, 1100, 680);

        JPanel barra = Tema.cabecera();
        barra.setBounds(0, 0, 1100, 72);
        fondo.add(barra);

        JPanel tarjeta = Tema.tarjeta();
        tarjeta.setBounds(315, 108, 470, 500);
        fondo.add(tarjeta);

        JLabel logo = new JLabel(Tema.logo(60));
        logo.setBounds(205, 34, 60, 60);
        tarjeta.add(logo);

        JLabel titulo = new JLabel("EurekaBank", SwingConstants.CENTER);
        titulo.setFont(Tema.fuente(Font.BOLD, 26));
        titulo.setForeground(Tema.NAVY);
        titulo.setBounds(0, 104, 466, 32);
        tarjeta.add(titulo);

        JLabel marca = new JLabel("Grupo 8 · Arquitectura de Software", SwingConstants.CENTER);
        marca.setFont(Tema.fuente(Font.PLAIN, 12));
        marca.setForeground(Tema.MUTED);
        marca.setBounds(0, 136, 466, 20);
        tarjeta.add(marca);

        JLabel subtitulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        subtitulo.setFont(Tema.fuente(Font.BOLD, 15));
        subtitulo.setForeground(Tema.TEAL);
        subtitulo.setBounds(0, 172, 466, 22);
        tarjeta.add(subtitulo);

        int x = 60;
        int ancho = 346;

        JLabel lblUsuario = Tema.etiqueta("Usuario");
        lblUsuario.setBounds(x, 214, ancho, 16);
        tarjeta.add(lblUsuario);

        campoUsuario = new JTextField();
        Tema.estilizarCampo(campoUsuario);
        campoUsuario.setBounds(x, 234, ancho, 44);
        tarjeta.add(campoUsuario);

        JLabel lblClave = Tema.etiqueta("Contraseña");
        lblClave.setBounds(x, 292, ancho, 16);
        tarjeta.add(lblClave);

        campoClave = new JPasswordField();
        Tema.estilizarCampo(campoClave);
        campoClave.setBounds(x, 312, ancho, 44);
        tarjeta.add(campoClave);

        botonLogin = Tema.botonPrimario("Iniciar Sesión");
        botonLogin.setBounds(x, 376, ancho, 48);
        tarjeta.add(botonLogin);

        etiquetaMensaje = new JLabel("", SwingConstants.CENTER);
        etiquetaMensaje.setFont(Tema.fuente(Font.PLAIN, 12));
        etiquetaMensaje.setForeground(Tema.ERROR);
        etiquetaMensaje.setBounds(40, 436, 386, 20);
        tarjeta.add(etiquetaMensaje);

        JLabel pie = new JLabel("Usuarios de prueba: MONSTER / MONSTER9 · admin / admin", SwingConstants.CENTER);
        pie.setFont(Tema.fuente(Font.PLAIN, 11));
        pie.setForeground(Tema.MUTED);
        pie.setBounds(20, 462, 426, 18);
        tarjeta.add(pie);

        configurarEventos();

        setContentPane(fondo);
    }

    private void configurarEventos() {
        Runnable autenticar = () -> {
            String usuario = campoUsuario.getText();
            String clave = new String(campoClave.getPassword());
            new LoginController(this).autenticar(usuario, clave);
        };
        botonLogin.addActionListener(e -> autenticar.run());
        campoClave.addActionListener(e -> autenticar.run());
    }

    public JTextField getTxtUsername() {
        return campoUsuario;
    }

    public JButton getBtnLogin() {
        return botonLogin;
    }

    public JLabel getMessageLabel() {
        return etiquetaMensaje;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Se mantiene el look and feel por defecto
        }
        EventQueue.invokeLater(() -> new LoginView().setVisible(true));
    }
}
