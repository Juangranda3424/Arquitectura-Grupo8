using System.Drawing;
using System.Globalization;
using System.Windows.Forms;
using WS_ConversionUnidades_Donet_CLIESC_GR8.Models;
using WS_ConversionUnidades_Donet_CLIESC_GR8.Services;

namespace WS_ConversionUnidades_Donet_CLIESC_GR8.Forms
{
    public class FrmConversor : Form
    {
        private readonly RestClienteServicio _rest;
        private readonly string _token;
        private readonly string _usuario;
        private readonly IReadOnlyDictionary<string, Unidad[]> _catalogo;
        private bool _sesionCerrada;

        private readonly ComboBox cmbMagnitud = new ComboBox();
        private readonly TextBox txtValor = new TextBox();
        private readonly ComboBox cmbOrigen = new ComboBox();
        private readonly ComboBox cmbDestino = new ComboBox();
        private readonly Button btnConvertir = new Button();
        private readonly Button btnCerrarSesion = new Button();
        private readonly Label lblResultado = new Label();
        private readonly Label lblError = new Label();

        // true cuando el usuario cerró sesión (o expiró): Program vuelve a mostrar el login
        public bool CerrarSesionSolicitada { get; private set; }

        public FrmConversor(RestClienteServicio rest, string token, string usuario,
            IReadOnlyDictionary<string, Unidad[]> catalogo)
        {
            _rest = rest;
            _token = token;
            _usuario = usuario;
            _catalogo = catalogo;
            ConstruirInterfaz();
        }

        private void ConstruirInterfaz()
        {
            Text = "Conversor de Unidades - GR8";
            Font = new Font("Segoe UI", 10F);
            ClientSize = new Size(460, 420);
            FormBorderStyle = FormBorderStyle.FixedSingle;
            MaximizeBox = false;
            StartPosition = FormStartPosition.CenterScreen;
            AcceptButton = btnConvertir;

            var lblSesion = new Label
            {
                Text = "Sesión: " + _usuario,
                Font = new Font("Segoe UI", 10F, FontStyle.Bold),
                Location = new Point(20, 18),
                Size = new Size(280, 24)
            };

            btnCerrarSesion.Text = "Cerrar sesión";
            btnCerrarSesion.Location = new Point(320, 12);
            btnCerrarSesion.Size = new Size(120, 32);
            btnCerrarSesion.Click += BtnCerrarSesion_Click;

            var lblMagnitud = new Label { Text = "Magnitud", Location = new Point(20, 60), AutoSize = true };
            cmbMagnitud.Location = new Point(20, 85);
            cmbMagnitud.Size = new Size(420, 27);
            cmbMagnitud.DropDownStyle = ComboBoxStyle.DropDownList;
            cmbMagnitud.DisplayMember = nameof(Unidad.Etiqueta);
            cmbMagnitud.ValueMember = nameof(Unidad.Valor);
            cmbMagnitud.DataSource = _catalogo.Keys
                .Select(clave => new Unidad(clave, UnidadesCatalogo.EtiquetaMagnitud(clave))).ToList();

            var lblValor = new Label { Text = "Valor", Location = new Point(20, 125), AutoSize = true };
            txtValor.Location = new Point(20, 150);
            txtValor.Size = new Size(420, 27);

            var lblDe = new Label { Text = "De", Location = new Point(20, 190), AutoSize = true };
            ConfigurarComboUnidad(cmbOrigen, new Point(20, 215));

            var lblA = new Label { Text = "A", Location = new Point(240, 190), AutoSize = true };
            ConfigurarComboUnidad(cmbDestino, new Point(240, 215));

            btnConvertir.Text = "Convertir";
            btnConvertir.Location = new Point(20, 265);
            btnConvertir.Size = new Size(420, 42);
            btnConvertir.BackColor = Color.FromArgb(37, 99, 235);
            btnConvertir.ForeColor = Color.White;
            btnConvertir.FlatStyle = FlatStyle.Flat;
            btnConvertir.Click += BtnConvertir_Click;

            lblResultado.Location = new Point(20, 322);
            lblResultado.Size = new Size(420, 44);
            lblResultado.Font = new Font("Segoe UI", 13F, FontStyle.Bold);
            lblResultado.ForeColor = Color.DarkGreen;
            lblResultado.TextAlign = ContentAlignment.MiddleCenter;

            lblError.Location = new Point(20, 370);
            lblError.Size = new Size(420, 40);
            lblError.ForeColor = Color.Firebrick;

            Controls.AddRange(new Control[]
            {
                lblSesion, btnCerrarSesion, lblMagnitud, cmbMagnitud, lblValor, txtValor,
                lblDe, cmbOrigen, lblA, cmbDestino, btnConvertir, lblResultado, lblError
            });

            // Se suscribe después de llenar el combo para no disparar el evento antes de tiempo
            cmbMagnitud.SelectedIndexChanged += (s, e) => CargarUnidades();
            CargarUnidades();
        }

        private static void ConfigurarComboUnidad(ComboBox combo, Point ubicacion)
        {
            combo.Location = ubicacion;
            combo.Size = new Size(200, 27);
            combo.DropDownStyle = ComboBoxStyle.DropDownList;
            combo.DisplayMember = nameof(Unidad.Etiqueta);
            combo.ValueMember = nameof(Unidad.Valor);
        }

        // Al cambiar la magnitud se recargan las unidades de origen y destino
        private void CargarUnidades()
        {
            if (cmbMagnitud.SelectedItem is not Unidad magnitud) return;
            var unidades = _catalogo[magnitud.Valor];

            // Una lista distinta por combo; si compartieran la lista, seleccionarían lo mismo a la vez
            cmbOrigen.DataSource = unidades.ToList();
            cmbDestino.DataSource = unidades.ToList();
            cmbOrigen.SelectedIndex = 0;
            cmbDestino.SelectedIndex = unidades.Length > 1 ? 1 : 0;

            lblResultado.Text = "";
            lblError.Text = "";
        }

        private async void BtnConvertir_Click(object? sender, EventArgs e)
        {
            lblError.Text = "";
            lblResultado.Text = "";

            if (!TryLeerValor(out var valor))
            {
                lblError.Text = "Ingresa un valor numérico válido.";
                return;
            }

            var magnitud = ((Unidad)cmbMagnitud.SelectedItem!).Valor;
            var origen = ((Unidad)cmbOrigen.SelectedItem!).Valor;
            var destino = ((Unidad)cmbDestino.SelectedItem!).Valor;

            btnConvertir.Enabled = false;
            Cursor = Cursors.WaitCursor;
            try
            {
                var resultado = await _rest.ConvertirAsync(_token, magnitud, origen, destino, valor);
                lblResultado.Text = $"{Formato(valor)} {origen.ToLowerInvariant()} = {Formato(resultado)} {destino.ToLowerInvariant()}";
            }
            catch (RestClienteException ex) when (ex.AccesoDenegado)
            {
                // El token ya no es válido (por ejemplo, el servidor se reinició): hay que iniciar sesión otra vez
                _sesionCerrada = true;
                MessageBox.Show(this, "Tu sesión expiró. Inicia sesión nuevamente.", "Sesión",
                    MessageBoxButtons.OK, MessageBoxIcon.Information);
                CerrarSesionSolicitada = true;
                Close();
            }
            catch (RestClienteException ex)
            {
                lblError.Text = ex.Message;
            }
            finally
            {
                btnConvertir.Enabled = true;
                Cursor = Cursors.Default;
            }
        }

        private async void BtnCerrarSesion_Click(object? sender, EventArgs e)
        {
            btnCerrarSesion.Enabled = false;
            if (!_sesionCerrada)
            {
                _sesionCerrada = true;
                try
                {
                    await _rest.LogoutAsync(_token);
                }
                catch (RestClienteException)
                {
                    // Si el servicio no responde, igual se cierra la sesión local
                }
            }

            CerrarSesionSolicitada = true;
            Close();
        }

        // Si se cierra la ventana con la X, se intenta invalidar el token en el servidor
        protected override void OnFormClosing(FormClosingEventArgs e)
        {
            base.OnFormClosing(e);
            if (_sesionCerrada) return;

            _sesionCerrada = true;
            try
            {
                Task.Run(() => _rest.LogoutAsync(_token)).Wait(TimeSpan.FromSeconds(3));
            }
            catch
            {
                // Sin conexión: no se bloquea el cierre de la aplicación
            }
        }

        // Acepta tanto 1.5 como 1,5
        private bool TryLeerValor(out double valor)
        {
            var texto = txtValor.Text.Trim().Replace(',', '.');
            return double.TryParse(texto, NumberStyles.Float, CultureInfo.InvariantCulture, out valor)
                   && !double.IsNaN(valor) && !double.IsInfinity(valor);
        }

        private static string Formato(double numero) => numero.ToString("0.##########", CultureInfo.InvariantCulture);
    }
}
