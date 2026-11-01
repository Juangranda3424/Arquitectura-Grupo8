package ec.edu.monster.view;

import javax.swing.*;
import java.awt.*;

/**
 * Menú principal del cliente de escritorio.
 */
public class MenuView extends JFrame {

    public MenuView() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("EurekaBank · Menú Principal");
        setSize(1100, 680);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel fondo = new JPanel(null);
        fondo.setBackground(Tema.FONDO);
        fondo.setBounds(0, 0, 1100, 680);

        JPanel barra = Tema.cabecera();
        barra.setBounds(0, 0, 1100, 72);

        JButton botonSalir = Tema.botonSecundario("Cerrar Sesión");
        botonSalir.setPreferredSize(new Dimension(150, 38));
        JPanel contenedorSalir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        contenedorSalir.setOpaque(false);
        contenedorSalir.add(botonSalir);
        barra.add(contenedorSalir, BorderLayout.EAST);
        fondo.add(barra);

        JPanel tarjeta = Tema.tarjeta();
        tarjeta.setBounds(50, 104, 1000, 540);
        fondo.add(tarjeta);

        JLabel titulo = Tema.titulo("Bienvenido a EurekaBank");
        titulo.setBounds(44, 34, 700, 34);
        tarjeta.add(titulo);

        JLabel ayuda = Tema.ayuda("Seleccione una operación del menú para continuar");
        ayuda.setBounds(44, 72, 700, 20);
        tarjeta.add(ayuda);

        int ancho = 170;
        int alto = 150;
        int separacion = 15;
        int x = 44;
        int y = 124;

        JButton botonDeposito = Tema.botonTarjeta("Depósito", "Abonar a una cuenta");
        botonDeposito.setBounds(x, y, ancho, alto);
        tarjeta.add(botonDeposito);

        x += ancho + separacion;
        JButton botonRetiro = Tema.botonTarjeta("Retiro", "Debitar de una cuenta");
        botonRetiro.setBounds(x, y, ancho, alto);
        tarjeta.add(botonRetiro);

        x += ancho + separacion;
        JButton botonTransferencia = Tema.botonTarjeta("Transferencia", "Mover dinero entre cuentas");
        botonTransferencia.setBounds(x, y, ancho, alto);
        tarjeta.add(botonTransferencia);

        x += ancho + separacion;
        JButton botonDatos = Tema.botonTarjeta("Datos de Cuenta", "Saldo y movimientos");
        botonDatos.setBounds(x, y, ancho, alto);
        tarjeta.add(botonDatos);

        x += ancho + separacion;
        JButton botonMovimientos = Tema.botonTarjeta("Movimientos", "Historial de la cuenta");
        botonMovimientos.setBounds(x, y, ancho, alto);
        tarjeta.add(botonMovimientos);

        JLabel divisor = new JLabel(" ");
        divisor.setOpaque(true);
        divisor.setBackground(Tema.BORDE);
        divisor.setBounds(44, 306, 908, 1);
        tarjeta.add(divisor);

        JLabel etiquetaServicio = Tema.etiqueta("Servicio RESTful");
        etiquetaServicio.setBounds(44, 330, 300, 16);
        tarjeta.add(etiquetaServicio);

        JLabel urlServicio = new JLabel("http://localhost:8080/WS_EurekaBank_Java_GR8/resources");
        urlServicio.setFont(Tema.fuente(Font.PLAIN, 13));
        urlServicio.setForeground(Tema.NAVY_2);
        urlServicio.setBounds(44, 350, 800, 20);
        tarjeta.add(urlServicio);

        JLabel etiquetaCliente = Tema.etiqueta("Cliente");
        etiquetaCliente.setBounds(44, 392, 300, 16);
        tarjeta.add(etiquetaCliente);

        JLabel cliente = new JLabel("CLIESC · Cliente de escritorio (Swing) · Grupo 8");
        cliente.setFont(Tema.fuente(Font.PLAIN, 13));
        cliente.setForeground(Tema.MUTED);
        cliente.setBounds(44, 412, 800, 20);
        tarjeta.add(cliente);

        botonDeposito.addActionListener(e -> abrir(new DepositoView()));
        botonRetiro.addActionListener(e -> abrir(new RetiroView()));
        botonTransferencia.addActionListener(e -> abrir(new TransferenciaView()));
        botonDatos.addActionListener(e -> abrir(new DatosView()));
        botonMovimientos.addActionListener(e -> abrir(new MovimientoView()));

        botonSalir.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginView login = new LoginView();
                login.setLocationRelativeTo(null);
                login.setVisible(true);
            });
        });

        setContentPane(fondo);
    }

    private void abrir(JFrame ventana) {
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
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
            // Look and feel por defecto
        }
        EventQueue.invokeLater(() -> new MenuView().setVisible(true));
    }
}
