using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controlador;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.modelo;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.vista
{
    public class FrmMovimientos : Form
    {
        private readonly EurekaBankController controlador = new EurekaBankController();
        private TextBox txtCuenta;
        private DataGridView tabla;
        private Label lblMensaje;
        private List<Movimiento> movimientos = new List<Movimiento>();
        private string filtro = string.Empty;

        public FrmMovimientos()
        {
            EstiloEurekaBank.PrepararFormulario(this, "Movimientos", 900, 640);
            FormBorderStyle = FormBorderStyle.Sizable;
            MaximizeBox = true;
            ConstruirVista();
        }

        private void ConstruirVista()
        {
            Controls.Add(EstiloEurekaBank.CrearEncabezado("Movimientos"));

            Panel tarjeta = EstiloEurekaBank.CrearTarjeta(24, 116, 852, 490);
            tarjeta.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Bottom;
            tarjeta.Controls.Add(EstiloEurekaBank.CrearTitulo("Consulta de movimientos", 24, 16));

            tarjeta.Controls.Add(EstiloEurekaBank.CrearEtiqueta("CUENTA", 24, 58));
            txtCuenta = EstiloEurekaBank.CrearCampo(24, 78, 280);
            tarjeta.Controls.Add(txtCuenta);

            Button btnBuscar = EstiloEurekaBank.CrearBoton("BUSCAR", 316, 76, 140, 30);
            btnBuscar.Click += BtnBuscar_Click;
            tarjeta.Controls.Add(btnBuscar);

            Button btnTodos = EstiloEurekaBank.CrearBoton("TODOS", 24, 124, 120, 32, true);
            btnTodos.Click += (s, e) => AplicarFiltro(string.Empty);
            tarjeta.Controls.Add(btnTodos);

            Button btnDepositos = EstiloEurekaBank.CrearBoton("DEPOSITOS", 152, 124, 130, 32, true);
            btnDepositos.Click += (s, e) => AplicarFiltro("Deposito");
            tarjeta.Controls.Add(btnDepositos);

            Button btnRetiros = EstiloEurekaBank.CrearBoton("RETIROS", 290, 124, 130, 32, true);
            btnRetiros.Click += (s, e) => AplicarFiltro("Retiro");
            tarjeta.Controls.Add(btnRetiros);

            Button btnTransferencias = EstiloEurekaBank.CrearBoton("TRANSFERENCIAS", 428, 124, 170, 32, true);
            btnTransferencias.Click += (s, e) => AplicarFiltro("Transferencia");
            tarjeta.Controls.Add(btnTransferencias);

            tabla = new DataGridView();
            tabla.Location = new Point(24, 172);
            tabla.Size = new Size(804, 250);
            tabla.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Bottom;
            ConfigurarTabla();
            tarjeta.Controls.Add(tabla);

            lblMensaje = EstiloEurekaBank.CrearMensaje(24, 432, 804);
            lblMensaje.Anchor = AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Bottom;
            tarjeta.Controls.Add(lblMensaje);

            Controls.Add(tarjeta);

            Button btnRegresar = EstiloEurekaBank.CrearBoton("REGRESAR AL MENU", 24, 616, 200, 34, true);
            btnRegresar.Anchor = AnchorStyles.Left | AnchorStyles.Bottom;
            btnRegresar.Click += (s, e) => Close();
            Controls.Add(btnRegresar);
        }

        private void ConfigurarTabla()
        {
            tabla.AutoGenerateColumns = false;
            tabla.BackgroundColor = EstiloEurekaBank.Superficie;
            tabla.BorderStyle = BorderStyle.None;
            tabla.GridColor = EstiloEurekaBank.Borde;
            tabla.RowHeadersVisible = false;
            tabla.ReadOnly = true;
            tabla.AllowUserToAddRows = false;
            tabla.AllowUserToDeleteRows = false;
            tabla.SelectionMode = DataGridViewSelectionMode.FullRowSelect;
            tabla.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            tabla.EnableHeadersVisualStyles = false;
            tabla.ColumnHeadersDefaultCellStyle.BackColor = EstiloEurekaBank.Primario;
            tabla.ColumnHeadersDefaultCellStyle.ForeColor = Color.White;
            tabla.ColumnHeadersDefaultCellStyle.Font = EstiloEurekaBank.Fuente(9.5F, FontStyle.Bold);
            tabla.ColumnHeadersHeight = 34;
            tabla.DefaultCellStyle.Font = EstiloEurekaBank.Fuente(9.5F);
            tabla.DefaultCellStyle.SelectionBackColor = Color.FromArgb(0xD7, 0xEE, 0xF2);
            tabla.DefaultCellStyle.SelectionForeColor = EstiloEurekaBank.Texto;
            tabla.RowTemplate.Height = 28;

            tabla.Columns.Add(new DataGridViewTextBoxColumn { DataPropertyName = "NroMov", HeaderText = "Nro. mov.", FillWeight = 70 });
            tabla.Columns.Add(new DataGridViewTextBoxColumn { DataPropertyName = "Fecha", HeaderText = "Fecha", FillWeight = 90 });
            tabla.Columns.Add(new DataGridViewTextBoxColumn { DataPropertyName = "Tipo", HeaderText = "Tipo", FillWeight = 120 });
            tabla.Columns.Add(new DataGridViewTextBoxColumn { DataPropertyName = "Accion", HeaderText = "Accion", FillWeight = 90 });
            tabla.Columns.Add(new DataGridViewTextBoxColumn { DataPropertyName = "Importe", HeaderText = "Importe", FillWeight = 90 });
            tabla.Columns.Add(new DataGridViewTextBoxColumn { DataPropertyName = "Cuenta", HeaderText = "Cuenta", FillWeight = 100 });
        }

        private void BtnBuscar_Click(object sender, EventArgs e)
        {
            string cuenta = txtCuenta.Text.Trim();
            if (string.IsNullOrEmpty(cuenta))
            {
                EstiloEurekaBank.MostrarError(lblMensaje, "Ingrese una cuenta valida.");
                return;
            }

            string mensaje;
            movimientos = controlador.ObtenerMovimientos(cuenta, out mensaje);

            if (string.IsNullOrEmpty(mensaje))
            {
                EstiloEurekaBank.MostrarExito(lblMensaje, "Se encontraron " + movimientos.Count + " movimientos.");
            }
            else
            {
                EstiloEurekaBank.MostrarError(lblMensaje, mensaje);
            }

            AplicarFiltro(filtro);
        }

        private void AplicarFiltro(string nuevoFiltro)
        {
            filtro = nuevoFiltro;

            List<Movimiento> visibles = string.IsNullOrEmpty(filtro)
                ? movimientos
                : movimientos.Where(m => m.Tipo != null && m.Tipo.IndexOf(filtro, StringComparison.OrdinalIgnoreCase) >= 0).ToList();

            tabla.DataSource = null;
            tabla.DataSource = visibles;
        }
    }
}
