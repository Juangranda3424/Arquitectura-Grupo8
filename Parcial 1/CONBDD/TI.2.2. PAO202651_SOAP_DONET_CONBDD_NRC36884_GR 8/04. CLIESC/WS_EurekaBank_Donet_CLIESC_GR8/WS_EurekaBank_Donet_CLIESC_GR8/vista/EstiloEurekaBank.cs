using System.Drawing.Drawing2D;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.vista
{
    /// <summary>
    /// Identidad visual compartida de EurekaBank Grupo 8 para los formularios Windows Forms.
    /// Todo el logotipo se dibuja por codigo (monograma "E" en un cuadrado redondeado).
    /// </summary>
    public static class EstiloEurekaBank
    {
        public static readonly Color Primario = Color.FromArgb(0x0B, 0x1F, 0x3A);
        public static readonly Color Primario2 = Color.FromArgb(0x13, 0x31, 0x5C);
        public static readonly Color Acento = Color.FromArgb(0x0F, 0xA3, 0xB1);
        public static readonly Color AcentoHover = Color.FromArgb(0x0C, 0x87, 0x92);
        public static readonly Color Ambar = Color.FromArgb(0xF2, 0xA5, 0x41);
        public static readonly Color Fondo = Color.FromArgb(0xF4, 0xF6, 0xFA);
        public static readonly Color Superficie = Color.White;
        public static readonly Color Texto = Color.FromArgb(0x1E, 0x29, 0x3B);
        public static readonly Color Atenuado = Color.FromArgb(0x64, 0x74, 0x8B);
        public static readonly Color Exito = Color.FromArgb(0x16, 0xA3, 0x4A);
        public static readonly Color Error = Color.FromArgb(0xDC, 0x26, 0x26);
        public static readonly Color Borde = Color.FromArgb(0xE2, 0xE8, 0xF0);

        public const string Marca = "EurekaBank";
        public const string Subtitulo = "Grupo 8 · Arquitectura de Software";

        public static Font Fuente(float tamano, FontStyle estilo = FontStyle.Regular)
        {
            return new Font("Segoe UI", tamano, estilo, GraphicsUnit.Point);
        }

        public static void PrepararFormulario(Form formulario, string titulo, int ancho, int alto)
        {
            formulario.Text = Marca + " · " + titulo;
            formulario.ClientSize = new Size(ancho, alto);
            formulario.BackColor = Fondo;
            formulario.Font = Fuente(10F);
            formulario.ForeColor = Texto;
            formulario.StartPosition = FormStartPosition.CenterScreen;
            formulario.FormBorderStyle = FormBorderStyle.FixedSingle;
            formulario.MaximizeBox = false;
        }

        /// <summary>
        /// Encabezado navy con el logotipo vectorial y el nombre de la marca.
        /// </summary>
        public static Panel CrearEncabezado(string titulo)
        {
            Panel encabezado = new Panel();
            encabezado.Dock = DockStyle.Top;
            encabezado.Height = 92;
            encabezado.BackColor = Primario;
            encabezado.Padding = new Padding(18, 14, 18, 14);

            LogoEureka logo = new LogoEureka();
            logo.Location = new Point(18, 16);
            logo.Size = new Size(58, 58);
            encabezado.Controls.Add(logo);

            Label marca = new Label();
            marca.Text = Marca;
            marca.Font = Fuente(19F, FontStyle.Bold);
            marca.ForeColor = Color.White;
            marca.AutoSize = true;
            marca.BackColor = Color.Transparent;
            marca.Location = new Point(90, 16);
            encabezado.Controls.Add(marca);

            Label subtitulo = new Label();
            subtitulo.Text = Subtitulo;
            subtitulo.Font = Fuente(9F);
            subtitulo.ForeColor = Color.FromArgb(0xC7, 0xD2, 0xE0);
            subtitulo.AutoSize = true;
            subtitulo.BackColor = Color.Transparent;
            subtitulo.Location = new Point(92, 50);
            encabezado.Controls.Add(subtitulo);

            Label seccion = new Label();
            seccion.Text = titulo.ToUpperInvariant();
            seccion.Font = Fuente(10F, FontStyle.Bold);
            seccion.ForeColor = Ambar;
            seccion.AutoSize = true;
            seccion.BackColor = Color.Transparent;
            seccion.Anchor = AnchorStyles.Top | AnchorStyles.Right;
            seccion.Location = new Point(encabezado.Width - 250, 36);
            seccion.TextAlign = ContentAlignment.MiddleRight;
            seccion.Size = new Size(230, 20);
            encabezado.Controls.Add(seccion);

            return encabezado;
        }

        /// <summary>
        /// Tarjeta blanca redondeada donde se apoyan los controles de cada pantalla.
        /// </summary>
        public static Panel CrearTarjeta(int x, int y, int ancho, int alto)
        {
            Panel tarjeta = new Panel();
            tarjeta.Location = new Point(x, y);
            tarjeta.Size = new Size(ancho, alto);
            tarjeta.BackColor = Superficie;
            tarjeta.Padding = new Padding(24);
            tarjeta.Paint += DibujarBordeRedondeado;
            return tarjeta;
        }

        public static Label CrearEtiqueta(string texto, int x, int y)
        {
            Label etiqueta = new Label();
            etiqueta.Text = texto;
            etiqueta.Font = Fuente(9F, FontStyle.Bold);
            etiqueta.ForeColor = Atenuado;
            etiqueta.AutoSize = true;
            etiqueta.Location = new Point(x, y);
            return etiqueta;
        }

        public static Label CrearTitulo(string texto, int x, int y)
        {
            Label titulo = new Label();
            titulo.Text = texto;
            titulo.Font = Fuente(15F, FontStyle.Bold);
            titulo.ForeColor = Primario;
            titulo.AutoSize = true;
            titulo.Location = new Point(x, y);
            return titulo;
        }

        public static TextBox CrearCampo(int x, int y, int ancho, bool esClave = false)
        {
            TextBox campo = new TextBox();
            campo.Location = new Point(x, y);
            campo.Width = ancho;
            campo.Font = Fuente(11F);
            campo.BorderStyle = BorderStyle.FixedSingle;
            campo.BackColor = Color.FromArgb(0xFB, 0xFC, 0xFE);
            campo.ForeColor = Texto;
            if (esClave)
            {
                campo.UseSystemPasswordChar = true;
            }

            return campo;
        }

        public static Button CrearBoton(string texto, int x, int y, int ancho, int alto, bool secundario = false)
        {
            Button boton = new Button();
            boton.Text = texto;
            boton.Location = new Point(x, y);
            boton.Size = new Size(ancho, alto);
            boton.FlatStyle = FlatStyle.Flat;
            boton.FlatAppearance.BorderSize = secundario ? 1 : 0;
            boton.FlatAppearance.BorderColor = Borde;
            boton.BackColor = secundario ? Color.White : Acento;
            boton.ForeColor = secundario ? Primario : Color.White;
            boton.Font = Fuente(10F, FontStyle.Bold);
            boton.Cursor = Cursors.Hand;
            boton.UseVisualStyleBackColor = false;

            if (!secundario)
            {
                boton.FlatAppearance.MouseOverBackColor = AcentoHover;
                boton.FlatAppearance.MouseDownBackColor = Primario2;
            }
            else
            {
                boton.FlatAppearance.MouseOverBackColor = Fondo;
            }

            return boton;
        }

        public static Label CrearMensaje(int x, int y, int ancho)
        {
            Label mensaje = new Label();
            mensaje.Location = new Point(x, y);
            mensaje.Size = new Size(ancho, 40);
            mensaje.Font = Fuente(9.5F, FontStyle.Bold);
            mensaje.ForeColor = Atenuado;
            mensaje.Text = string.Empty;
            return mensaje;
        }

        public static void MostrarExito(Label etiqueta, string texto)
        {
            etiqueta.ForeColor = Exito;
            etiqueta.Text = texto;
        }

        public static void MostrarError(Label etiqueta, string texto)
        {
            etiqueta.ForeColor = Error;
            etiqueta.Text = texto;
        }

        private static void DibujarBordeRedondeado(object sender, PaintEventArgs e)
        {
            Panel panel = sender as Panel;
            if (panel == null)
            {
                return;
            }

            e.Graphics.SmoothingMode = SmoothingMode.AntiAlias;
            using (GraphicsPath ruta = RutaRedondeada(new Rectangle(0, 0, panel.Width - 1, panel.Height - 1), 12))
            using (Pen lapiz = new Pen(Borde, 1))
            {
                e.Graphics.DrawPath(lapiz, ruta);
            }
        }

        public static GraphicsPath RutaRedondeada(Rectangle area, int radio)
        {
            GraphicsPath ruta = new GraphicsPath();
            int diametro = radio * 2;
            ruta.AddArc(area.X, area.Y, diametro, diametro, 180, 90);
            ruta.AddArc(area.Right - diametro, area.Y, diametro, diametro, 270, 90);
            ruta.AddArc(area.Right - diametro, area.Bottom - diametro, diametro, diametro, 0, 90);
            ruta.AddArc(area.X, area.Bottom - diametro, diametro, diametro, 90, 90);
            ruta.CloseFigure();
            return ruta;
        }
    }

    /// <summary>
    /// Logotipo vectorial: monograma "E" blanco dentro de un cuadrado navy redondeado
    /// con acento ambar.
    /// </summary>
    public class LogoEureka : Control
    {
        public LogoEureka()
        {
            SetStyle(ControlStyles.AllPaintingInWmPaint | ControlStyles.OptimizedDoubleBuffer | ControlStyles.UserPaint | ControlStyles.SupportsTransparentBackColor, true);
            BackColor = Color.Transparent;
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            e.Graphics.SmoothingMode = SmoothingMode.AntiAlias;

            int lado = Math.Min(Width, Height);
            Rectangle cuadrado = new Rectangle(0, 0, lado - 1, lado - 1);

            using (GraphicsPath ruta = EstiloEurekaBank.RutaRedondeada(cuadrado, Math.Max(6, lado / 5)))
            using (SolidBrush fondo = new SolidBrush(EstiloEurekaBank.Primario2))
            using (Pen borde = new Pen(EstiloEurekaBank.Acento, 2F))
            {
                e.Graphics.FillPath(fondo, ruta);
                e.Graphics.DrawPath(borde, ruta);
            }

            using (SolidBrush acento = new SolidBrush(EstiloEurekaBank.Ambar))
            {
                e.Graphics.FillRectangle(acento, 6, lado - 12, lado - 12, 4);
            }

            using (Font fuente = new Font("Segoe UI", lado * 0.52F, FontStyle.Bold, GraphicsUnit.Pixel))
            using (SolidBrush blanco = new SolidBrush(Color.White))
            using (StringFormat formato = new StringFormat { Alignment = StringAlignment.Center, LineAlignment = StringAlignment.Center })
            {
                Rectangle areaTexto = new Rectangle(0, 0, lado, lado - 4);
                e.Graphics.DrawString("E", fuente, blanco, areaTexto, formato);
            }
        }
    }
}
