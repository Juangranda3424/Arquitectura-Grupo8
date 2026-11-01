package ec.edu.monster.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Identidad visual compartida de EurekaBank · Grupo 8 para los clientes de
 * escritorio (Swing).
 *
 * <p>Paleta: azul marino #0B1F3A / #13315C, acento teal #0FA3B1, resaltado ámbar
 * #F2A541, fondo #F4F6FA, superficie #FFFFFF, texto #1E293B, texto suave
 * #64748B, borde #E2E8F0, éxito #16A34A y error #DC2626. Tipografía Segoe UI,
 * tarjetas redondeadas de 12 px y botones planos teal.</p>
 */
public final class Tema {

    public static final Color AZUL_MARINO = new Color(0x0B, 0x1F, 0x3A);
    public static final Color AZUL_MARINO_2 = new Color(0x13, 0x31, 0x5C);
    public static final Color TEAL = new Color(0x0F, 0xA3, 0xB1);
    public static final Color TEAL_OSCURO = new Color(0x0B, 0x84, 0x8F);
    public static final Color AMBAR = new Color(0xF2, 0xA5, 0x41);
    public static final Color FONDO = new Color(0xF4, 0xF6, 0xFA);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color TEXTO = new Color(0x1E, 0x29, 0x3B);
    public static final Color TEXTO_SUAVE = new Color(0x64, 0x74, 0x8B);
    public static final Color EXITO = new Color(0x16, 0xA3, 0x4A);
    public static final Color EXITO_FONDO = new Color(0xE7, 0xF7, 0xEC);
    public static final Color ERROR = new Color(0xDC, 0x26, 0x26);
    public static final Color ERROR_FONDO = new Color(0xFD, 0xE8, 0xE8);
    public static final Color BORDE = new Color(0xE2, 0xE8, 0xF0);
    public static final Color SOMBRA = new Color(0x0B, 0x1F, 0x3A, 20);

    public static final String MARCA = "EUREKABANK";
    public static final String SUBTITULO_MARCA = "Grupo 8 · Arquitectura de Software";
    public static final int RADIO = 12;
    public static final int ALTO_BANNER = 96;

    private static final String FAMILIA = "Segoe UI";

    private Tema() {
    }

    public static Font fuente(int estilo, int tamano) {
        return new Font(FAMILIA, estilo, tamano);
    }

    /** Panel con esquinas redondeadas (tarjeta) de la nueva identidad. */
    public static class Tarjeta extends JPanel {
        private final int radio;
        private Color relleno = SUPERFICIE;

        public Tarjeta() {
            this(RADIO);
        }

        public Tarjeta(int radio) {
            this.radio = radio;
            setOpaque(false);
            setLayout(null);
        }

        public void setRelleno(Color color) {
            this.relleno = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(SOMBRA);
            g2.fill(new RoundRectangle2D.Float(1, 3, getWidth() - 2, getHeight() - 3, radio, radio));
            RoundRectangle2D forma = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, radio, radio);
            g2.setColor(relleno);
            g2.fill(forma);
            g2.setColor(BORDE);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(forma);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Logotipo vectorial: monograma "E" en cuadrado redondeado. */
    public static class Logo implements Icon {
        private final int tamano;

        public Logo(int tamano) {
            this.tamano = tamano;
        }

        @Override
        public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float radio = tamano * 0.28f;
            g2.setColor(TEAL);
            g2.fill(new RoundRectangle2D.Float(x, y, tamano, tamano, radio, radio));
            g2.setColor(AMBAR);
            g2.setStroke(new BasicStroke(Math.max(2f, tamano * 0.06f)));
            g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, tamano - 2, tamano - 2, radio, radio));

            int tamLetra = Math.round(tamano * 0.6f);
            g2.setFont(fuente(Font.BOLD, tamLetra));
            FontMetrics fm = g2.getFontMetrics();
            int lx = x + (tamano - fm.stringWidth("E")) / 2;
            int ly = y + (tamano - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString("E", lx, ly);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return tamano;
        }

        @Override
        public int getIconHeight() {
            return tamano;
        }
    }

    /** Barra superior azul marino con logotipo, marca y subtítulo. */
    public static JPanel banner(int ancho, String pantalla) {
        JPanel barra = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(AZUL_MARINO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(AZUL_MARINO_2);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.setColor(AMBAR);
                g2.fillRect(0, getHeight() - 4, Math.min(220, getWidth()), 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        barra.setOpaque(false);
        barra.setBounds(0, 0, ancho, ALTO_BANNER);

        JLabel logo = new JLabel(new Logo(44));
        logo.setBounds(36, 26, 44, 44);
        barra.add(logo);

        JLabel marca = new JLabel(MARCA);
        marca.setFont(fuente(Font.BOLD, 22));
        marca.setForeground(Color.WHITE);
        marca.setBounds(92, 24, 420, 28);
        barra.add(marca);

        JLabel sub = new JLabel(SUBTITULO_MARCA);
        sub.setFont(fuente(Font.PLAIN, 12));
        sub.setForeground(new Color(0xC7, 0xD2, 0xE0));
        sub.setBounds(92, 52, 420, 18);
        barra.add(sub);

        if (pantalla != null && !pantalla.isEmpty()) {
            JLabel seccion = new JLabel(pantalla.toUpperCase());
            seccion.setFont(fuente(Font.BOLD, 12));
            seccion.setForeground(AMBAR);
            seccion.setHorizontalAlignment(JLabel.RIGHT);
            seccion.setBounds(ancho - 420, 38, 380, 18);
            barra.add(seccion);
        }
        return barra;
    }

    /** Fondo del marco: superficie clara con la barra superior. */
    public static JPanel fondo(int ancho, int alto, String pantalla) {
        JPanel fondo = new JPanel(null);
        fondo.setBackground(FONDO);
        fondo.setBounds(0, 0, ancho, alto);
        fondo.add(banner(ancho, pantalla));
        return fondo;
    }

    public static JLabel titulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente(Font.BOLD, 24));
        lbl.setForeground(AZUL_MARINO);
        return lbl;
    }

    public static JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente(Font.BOLD, 12));
        lbl.setForeground(TEXTO_SUAVE);
        return lbl;
    }

    public static JLabel texto(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente(Font.PLAIN, 14));
        lbl.setForeground(TEXTO);
        return lbl;
    }

    public static JLabel valor(String texto, int tamano, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente(Font.BOLD, tamano));
        lbl.setForeground(color);
        return lbl;
    }

    public static JTextField campo() {
        JTextField campo = new JTextField();
        campo.setFont(fuente(Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setBackground(SUPERFICIE);
        campo.setCaretColor(TEAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        return campo;
    }

    public static JPasswordField campoClave() {
        JPasswordField campo = new JPasswordField();
        campo.setFont(fuente(Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setBackground(SUPERFICIE);
        campo.setCaretColor(TEAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        return campo;
    }

    /** Botón plano con esquinas de 8 px; teal para la acción principal. */
    public static class Boton extends JButton {
        private final Color fondo;
        private final Color fondoSobre;
        private final Color colorTexto;
        private final boolean conBorde;
        private boolean sobre;

        public Boton(String etiqueta, Color fondo, Color fondoSobre, Color colorTexto, boolean conBorde) {
            super(etiqueta);
            this.fondo = fondo;
            this.fondoSobre = fondoSobre;
            this.colorTexto = colorTexto;
            this.conBorde = conBorde;
            setForeground(colorTexto);
            setFont(fuente(Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setRolloverEnabled(true);
            setUI(new BasicButtonUI());
            setBorder(new EmptyBorder(10, 18, 10, 18));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    sobre = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    sobre = false;
                    repaint();
                }
            });
        }

        @Override
        public void setEnabled(boolean habilitado) {
            super.setEnabled(habilitado);
            setForeground(habilitado ? colorTexto : TEXTO_SUAVE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D forma = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(isEnabled() ? (sobre ? fondoSobre : fondo) : FONDO);
            g2.fill(forma);
            if (conBorde) {
                g2.setColor(BORDE);
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Botón plano teal (acción principal). */
    public static Boton botonPrimario(String etiqueta) {
        return new Boton(etiqueta, TEAL, TEAL_OSCURO, Color.WHITE, false);
    }

    /** Botón secundario blanco con borde. */
    public static Boton botonSecundario(String etiqueta) {
        return new Boton(etiqueta, SUPERFICIE, FONDO, AZUL_MARINO, true);
    }

    /** Banner de mensajes (éxito / error) dentro de una tarjeta. */
    public static class Mensaje extends JPanel {
        private final JLabel texto = new JLabel(" ");
        private Color fondo = Tema.EXITO_FONDO;

        public Mensaje() {
            setLayout(new java.awt.BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));
            texto.setFont(fuente(Font.PLAIN, 13));
            texto.setForeground(EXITO);
            add(texto, java.awt.BorderLayout.WEST);
            setVisible(false);
        }

        public void exito(String mensaje) {
            fondo = Tema.EXITO_FONDO;
            texto.setForeground(Tema.EXITO);
            texto.setText(mensaje);
            setVisible(true);
            repaint();
        }

        public void error(String mensaje) {
            fondo = Tema.ERROR_FONDO;
            texto.setForeground(Tema.ERROR);
            texto.setText(mensaje);
            setVisible(true);
            repaint();
        }

        public void limpiar() {
            texto.setText(" ");
            setVisible(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fondo);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Tamaño preferido cómodo para un botón de ancho fijo. */
    public static Dimension ancho(int ancho, int alto) {
        return new Dimension(ancho, alto);
    }
}
