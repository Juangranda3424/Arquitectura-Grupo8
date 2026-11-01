package ec.edu.monster.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Identidad visual compartida de EurekaBank · Grupo 8 para el cliente de
 * escritorio (Swing).
 *
 * <p>Paleta, tipografía y componentes base (logo vectorial, tarjetas redondeadas
 * y botones planos) usados por todas las pantallas del cliente.</p>
 */
public final class Tema {

    public static final Color NAVY = new Color(0x0B1F3A);
    public static final Color NAVY_2 = new Color(0x13315C);
    public static final Color TEAL = new Color(0x0FA3B1);
    public static final Color TEAL_CLARO = new Color(0xE6F6F8);
    public static final Color AMBER = new Color(0xF2A541);
    public static final Color FONDO = new Color(0xF4F6FA);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color TEXTO = new Color(0x1E293B);
    public static final Color MUTED = new Color(0x64748B);
    public static final Color EXITO = new Color(0x16A34A);
    public static final Color EXITO_FONDO = new Color(0xE7F7EC);
    public static final Color ERROR = new Color(0xDC2626);
    public static final Color ERROR_FONDO = new Color(0xFDECEC);
    public static final Color BORDE = new Color(0xE2E8F0);

    private static final String TIPOGRAFIA = "Segoe UI";

    private Tema() {
    }

    public static Font fuente(int estilo, int tamano) {
        return new Font(TIPOGRAFIA, estilo, tamano);
    }

    // ------------------------------------------------------------------
    // Logotipo (vectorial): monograma "E" en cuadrado redondeado
    // ------------------------------------------------------------------
    public static Icon logo(final int lado) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(TEAL);
                g2.fill(new RoundRectangle2D.Float(0, 0, lado, lado, lado / 3f, lado / 3f));

                float margen = lado * 0.28f;
                float ancho = lado - 2 * margen;
                float alto = lado - 2 * margen;
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(Math.max(2f, lado * 0.11f), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                g2.drawLine((int) (margen + ancho), (int) margen, (int) margen, (int) margen);
                g2.drawLine((int) margen, (int) margen, (int) margen, (int) (margen + alto));
                g2.drawLine((int) margen, (int) (margen + alto), (int) (margen + ancho), (int) (margen + alto));
                g2.drawLine((int) margen, (int) (margen + alto / 2), (int) (margen + ancho * 0.7f), (int) (margen + alto / 2));
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return lado;
            }

            @Override
            public int getIconHeight() {
                return lado;
            }
        };
    }

    // ------------------------------------------------------------------
    // Estructuras de pantalla
    // ------------------------------------------------------------------

    /** Barra superior navy con el logotipo y el nombre de la marca. */
    public static JPanel cabecera() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(NAVY);
        barra.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        marca.setOpaque(false);

        JLabel icono = new JLabel(logo(40));
        marca.add(icono);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        JLabel nombre = new JLabel("EurekaBank");
        nombre.setFont(fuente(Font.BOLD, 20));
        nombre.setForeground(Color.WHITE);
        JLabel subtitulo = new JLabel("Grupo 8 · Arquitectura de Software");
        subtitulo.setFont(fuente(Font.PLAIN, 11));
        subtitulo.setForeground(new Color(0x9FB3CC));
        textos.add(nombre);
        textos.add(subtitulo);
        marca.add(textos);

        barra.add(marca, BorderLayout.WEST);
        return barra;
    }

    /** Tarjeta blanca redondeada (contenedor base de cada pantalla). */
    public static JPanel tarjeta() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fill(new RoundRectangle2D.Float(2, 4, getWidth() - 4, getHeight() - 6, 16, 16));
                g2.setColor(SUPERFICIE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 6, 16, 16));
                g2.setColor(BORDE);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 5, getHeight() - 7, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    /** Título de pantalla. */
    public static JLabel titulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente(Font.BOLD, 26));
        label.setForeground(NAVY);
        return label;
    }

    /** Subtítulo / ayuda de pantalla. */
    public static JLabel ayuda(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente(Font.PLAIN, 13));
        label.setForeground(MUTED);
        return label;
    }

    /** Etiqueta de campo de formulario. */
    public static JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto.toUpperCase());
        label.setFont(fuente(Font.BOLD, 11));
        label.setForeground(MUTED);
        return label;
    }

    /** Aplica el estilo de campo de texto de la marca. */
    public static void estilizarCampo(JTextField campo) {
        campo.setFont(fuente(Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setBackground(SUPERFICIE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 2, true),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        campo.setCaretColor(NAVY);
    }

    // ------------------------------------------------------------------
    // Botones
    // ------------------------------------------------------------------

    /** Botón plano teal (acción principal). */
    public static JButton botonPrimario(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x0FB9C9) : TEAL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setForeground(Color.WHITE);
        boton.setFont(fuente(Font.BOLD, 14));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        return boton;
    }

    /** Botón secundario (contorno). */
    public static JButton botonSecundario(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? FONDO : SUPERFICIE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(getModel().isRollover() ? TEAL : BORDE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setForeground(NAVY_2);
        boton.setFont(fuente(Font.BOLD, 13));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return boton;
    }

    /** Tarjeta-botón del menú principal (título + descripción). */
    public static JButton botonTarjeta(String titulo, String descripcion) {
        JButton boton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean activo = getModel().isRollover();
                g2.setColor(activo ? TEAL_CLARO : SUPERFICIE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(activo ? TEAL : BORDE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));

                g2.setFont(fuente(Font.BOLD, 15));
                g2.setColor(NAVY);
                dibujarCentrado(g2, titulo, getWidth(), getHeight() / 2 - 4);
                g2.setFont(fuente(Font.PLAIN, 11));
                g2.setColor(MUTED);
                dibujarCentrado(g2, descripcion, getWidth(), getHeight() / 2 + 18);
                g2.dispose();
            }
        };
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder());
        return boton;
    }

    private static void dibujarCentrado(Graphics2D g2, String texto, int ancho, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (ancho - fm.stringWidth(texto)) / 2;
        g2.drawString(texto, x, y);
    }

    // ------------------------------------------------------------------
    // Banner de mensajes (éxito / error)
    // ------------------------------------------------------------------
    public static final class Banner extends JPanel {

        private final JLabel etiquetaTexto = new JLabel();
        private Color fondo;

        public Banner() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 14, 9));
            setOpaque(false);
            etiquetaTexto.setFont(fuente(Font.PLAIN, 13));
            add(etiquetaTexto);
            setVisible(false);
        }

        public void exito(String texto) {
            mostrar(Tema.EXITO_FONDO, Tema.EXITO, texto);
        }

        public void error(String texto) {
            mostrar(Tema.ERROR_FONDO, Tema.ERROR, texto);
        }

        public void ocultar() {
            fondo = null;
            etiquetaTexto.setText("");
            setVisible(false);
        }

        private void mostrar(Color fondo, Color texto, String mensaje) {
            this.fondo = fondo;
            etiquetaTexto.setForeground(texto);
            etiquetaTexto.setText(mensaje);
            setVisible(true);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (fondo != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }

    /** Barra lateral/etiqueta de valor destacado. */
    public static JLabel valor(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente(Font.BOLD, 15));
        label.setForeground(AMBER);
        return label;
    }
}
