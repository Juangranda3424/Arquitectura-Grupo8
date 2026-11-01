using System;
using System.Drawing;
using System.Windows.Forms;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.view
{
    /// <summary>
    /// Ventana base con la identidad compartida: cabecera navy con el logotipo,
    /// zona de contenido sobre fondo claro y barra de estado inferior.
    /// </summary>
    public class BaseForm : Form
    {
        protected Panel Contenido;
        protected Label Estado;

        private readonly string tituloPantalla;

        public BaseForm(string tituloPantalla)
        {
            this.tituloPantalla = tituloPantalla;

            Text = "EurekaBank · " + tituloPantalla;
            StartPosition = FormStartPosition.CenterScreen;
            FormBorderStyle = FormBorderStyle.FixedSingle;
            MaximizeBox = false;
            BackColor = Theme.Background;
            Font = Theme.FuenteBase;
            ClientSize = new Size(880, 620);

            ConstruirCabecera();
            ConstruirEstado();
            ConstruirContenido();
        }

        private void ConstruirCabecera()
        {
            var cabecera = new Panel
            {
                Dock = DockStyle.Top,
                Height = 84,
                BackColor = Theme.Navy
            };

            var logo = new LogoControl
            {
                Location = new Point(24, 20),
                Size = new Size(44, 44)
            };

            var marca = new Label
            {
                Text = "EurekaBank",
                Font = Theme.FuenteMarca,
                ForeColor = Color.White,
                AutoSize = true,
                Location = new Point(80, 20)
            };

            var subtitulo = new Label
            {
                Text = "Grupo 8 · Arquitectura de Software",
                Font = new Font("Segoe UI", 8.5F, FontStyle.Regular),
                ForeColor = Color.FromArgb(168, 190, 214),
                AutoSize = true,
                Location = new Point(82, 48)
            };

            var titulo = new Label
            {
                Text = tituloPantalla.ToUpperInvariant(),
                Font = new Font("Segoe UI Semibold", 11F, FontStyle.Bold),
                ForeColor = Theme.Amber,
                AutoSize = false,
                TextAlign = ContentAlignment.MiddleRight,
                Dock = DockStyle.Right,
                Width = 320,
                Padding = new Padding(0, 0, 24, 0)
            };

            cabecera.Controls.Add(logo);
            cabecera.Controls.Add(marca);
            cabecera.Controls.Add(subtitulo);
            cabecera.Controls.Add(titulo);

            Controls.Add(cabecera);
        }

        private void ConstruirEstado()
        {
            Estado = new Label
            {
                Dock = DockStyle.Bottom,
                Height = 32,
                BackColor = Color.White,
                ForeColor = Theme.Muted,
                TextAlign = ContentAlignment.MiddleLeft,
                Padding = new Padding(24, 0, 0, 0),
                Text = "Conectado a WS_EurekaBank_Donet_GR8"
            };

            Controls.Add(Estado);
        }

        private void ConstruirContenido()
        {
            Contenido = new Panel
            {
                Dock = DockStyle.Fill,
                BackColor = Theme.Background,
                Padding = new Padding(0)
            };

            Controls.Add(Contenido);
            Contenido.BringToFront();
        }

        // ---- utilidades compartidas de UI ----

        private const int Margen = 24;

        protected CardPanel NuevaTarjeta(int x, int y, int ancho, int alto)
        {
            int anchoMaximo = Contenido.ClientSize.Width - (2 * Margen) - x;
            if (anchoMaximo > 0 && ancho > anchoMaximo)
            {
                ancho = anchoMaximo;
            }

            var tarjeta = new CardPanel
            {
                Location = new Point(x + Margen, y + Margen),
                Size = new Size(ancho, alto)
            };
            Contenido.Controls.Add(tarjeta);
            return tarjeta;
        }

        protected static Label Etiqueta(string texto)
        {
            return new Label
            {
                Text = texto,
                Font = Theme.FuenteEtiqueta,
                ForeColor = Theme.Text,
                AutoSize = true
            };
        }

        protected static TextBox Campo(bool password = false)
        {
            var caja = new TextBox
            {
                Font = new Font("Segoe UI", 11F, FontStyle.Regular),
                BorderStyle = BorderStyle.FixedSingle,
                ForeColor = Theme.Text,
                BackColor = Color.White
            };

            if (password)
            {
                caja.UseSystemPasswordChar = true;
            }

            return caja;
        }

        protected static void MostrarOk(Label destino, string mensaje)
        {
            destino.Text = "✔  " + mensaje;
            destino.ForeColor = Theme.Success;
            destino.BackColor = Theme.SuccessBg;
            destino.Visible = true;
        }

        protected static void MostrarError(Label destino, string mensaje)
        {
            destino.Text = "✖  " + mensaje;
            destino.ForeColor = Theme.Error;
            destino.BackColor = Theme.ErrorBg;
            destino.Visible = true;
        }

        protected void Navegar(Form siguiente)
        {
            Hide();
            siguiente.FormClosed += (s, e) => Show();
            siguiente.Show(this);
        }
    }
}
