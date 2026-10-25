using System.Drawing;
using System.Windows.Forms;
using WS_ConversionUnidades_Donet_CLIESC_GR8.Models;
using WS_ConversionUnidades_Donet_CLIESC_GR8.Services;

namespace WS_ConversionUnidades_Donet_CLIESC_GR8.Forms
{
    public class FrmLogin : Form
    {
        private readonly RestClienteServicio _rest;

        private readonly TextBox txtUsuario = new TextBox();
        private readonly TextBox txtContrasena = new TextBox();
        private readonly Button btnIngresar = new Button();
        private readonly Label lblError = new Label();

        public string? Token { get; private set; }
        public string? Usuario { get; private set; }

        // Unidades por magnitud obtenidas del servicio (GET /catalogo) justo después de iniciar sesión
        public IReadOnlyDictionary<string, Unidad[]>? Catalogo { get; private set; }

        public FrmLogin(RestClienteServicio rest)
        {
            _rest = rest;
            ConstruirInterfaz();
        }

        private void ConstruirInterfaz()
        {
            Text = "Iniciar sesión - Conversión de Unidades GR8";
            Font = new Font("Segoe UI", 10F);
            ClientSize = new Size(360, 290);
            FormBorderStyle = FormBorderStyle.FixedDialog;
            MaximizeBox = false;
            MinimizeBox = false;
            StartPosition = FormStartPosition.CenterScreen;
            AcceptButton = btnIngresar;

            var titulo = new Label
            {
                Text = "Iniciar sesión",
                Font = new Font("Segoe UI", 16F, FontStyle.Bold),
                Location = new Point(20, 15),
                AutoSize = true
            };

            var lblUsuario = new Label { Text = "Usuario", Location = new Point(20, 65), AutoSize = true };
            txtUsuario.Location = new Point(20, 90);
            txtUsuario.Size = new Size(320, 27);

            var lblContrasena = new Label { Text = "Contraseña", Location = new Point(20, 130), AutoSize = true };
            txtContrasena.Location = new Point(20, 155);
            txtContrasena.Size = new Size(320, 27);
            txtContrasena.UseSystemPasswordChar = true;

            lblError.Location = new Point(20, 190);
            lblError.Size = new Size(320, 40);
            lblError.ForeColor = Color.Firebrick;

            btnIngresar.Text = "Ingresar";
            btnIngresar.Location = new Point(20, 235);
            btnIngresar.Size = new Size(320, 40);
            btnIngresar.BackColor = Color.FromArgb(37, 99, 235);
            btnIngresar.ForeColor = Color.White;
            btnIngresar.FlatStyle = FlatStyle.Flat;
            btnIngresar.Click += BtnIngresar_Click;

            Controls.AddRange(new Control[]
            {
                titulo, lblUsuario, txtUsuario, lblContrasena, txtContrasena, lblError, btnIngresar
            });
        }

        private async void BtnIngresar_Click(object? sender, EventArgs e)
        {
            lblError.Text = "";
            var usuario = txtUsuario.Text.Trim();

            if (usuario.Length == 0 || txtContrasena.Text.Length == 0)
            {
                lblError.Text = "Ingresa tu usuario y contraseña.";
                return;
            }

            btnIngresar.Enabled = false;
            Cursor = Cursors.WaitCursor;
            string? token = null;
            try
            {
                token = await _rest.LoginAsync(usuario, txtContrasena.Text);
                Catalogo = await _rest.ObtenerCatalogoAsync(token);
                Token = token;
                Usuario = usuario;
                DialogResult = DialogResult.OK;
            }
            catch (RestClienteException ex)
            {
                // Si el login funcionó pero no llegó el catálogo, se invalida el token para no dejar la sesión abierta
                if (token != null) await CerrarSesionSilenciosaAsync(token);

                lblError.Text = ex.Message;
                txtContrasena.Clear();
                txtContrasena.Focus();
            }
            finally
            {
                btnIngresar.Enabled = true;
                Cursor = Cursors.Default;
            }
        }

        private async Task CerrarSesionSilenciosaAsync(string token)
        {
            try
            {
                await _rest.LogoutAsync(token);
            }
            catch (RestClienteException)
            {
                // Sin conexión: no hay nada más que hacer
            }
        }
    }
}
