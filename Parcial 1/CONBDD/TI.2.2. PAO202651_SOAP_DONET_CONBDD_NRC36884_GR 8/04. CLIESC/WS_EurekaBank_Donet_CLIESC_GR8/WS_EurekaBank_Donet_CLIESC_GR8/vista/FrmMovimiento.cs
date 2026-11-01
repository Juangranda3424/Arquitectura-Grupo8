using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controlador;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.modelo;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.vista
{
    public class FrmMovimiento : Form
    {
        private sealed class TipoMovimientoItem
        {
            public string Texto { get; set; }
            public string Codigo { get; set; }

            public override string ToString()
            {
                return Texto;
            }
        }

        private readonly EurekaBankController controlador = new EurekaBankController();
        private TextBox txtCuenta;
        private TextBox txtImporte;
        private ComboBox cmbTipo;
        private TextBox txtCuentaDestino;
        private Label lblMensaje;

        public FrmMovimiento()
        {
            EstiloEurekaBank.PrepararFormulario(this, "Registrar movimiento", 520, 620);
            ConstruirVista();
        }

        private void ConstruirVista()
        {
            Controls.Add(EstiloEurekaBank.CrearEncabezado("Registrar movimiento"));

            Panel tarjeta = EstiloEurekaBank.CrearTarjeta(30, 122, 460, 450);
            tarjeta.Controls.Add(EstiloEurekaBank.CrearTitulo("Nuevo movimiento", 24, 16));

            tarjeta.Controls.Add(EstiloEurekaBank.CrearEtiqueta("CUENTA", 24, 62));
            txtCuenta = EstiloEurekaBank.CrearCampo(24, 82, 400);
            tarjeta.Controls.Add(txtCuenta);

            tarjeta.Controls.Add(EstiloEurekaBank.CrearEtiqueta("IMPORTE", 24, 120));
            txtImporte = EstiloEurekaBank.CrearCampo(24, 140, 400);
            tarjeta.Controls.Add(txtImporte);

            tarjeta.Controls.Add(EstiloEurekaBank.CrearEtiqueta("TIPO DE MOVIMIENTO", 24, 178));
            cmbTipo = new ComboBox();
            cmbTipo.Location = new Point(24, 198);
            cmbTipo.Width = 400;
            cmbTipo.DropDownStyle = ComboBoxStyle.DropDownList;
            cmbTipo.Font = EstiloEurekaBank.Fuente(11F);
            cmbTipo.Items.Add(new TipoMovimientoItem { Texto = "Deposito", Codigo = EurekaBankModel.Deposito });
            cmbTipo.Items.Add(new TipoMovimientoItem { Texto = "Retiro", Codigo = EurekaBankModel.Retiro });
            cmbTipo.Items.Add(new TipoMovimientoItem { Texto = "Transferencia", Codigo = EurekaBankModel.Transferencia });
            cmbTipo.SelectedIndex = 0;
            cmbTipo.SelectedIndexChanged += CmbTipo_SelectedIndexChanged;
            tarjeta.Controls.Add(cmbTipo);

            tarjeta.Controls.Add(EstiloEurekaBank.CrearEtiqueta("CUENTA DESTINO (solo transferencia)", 24, 238));
            txtCuentaDestino = EstiloEurekaBank.CrearCampo(24, 258, 400);
            txtCuentaDestino.Enabled = false;
            tarjeta.Controls.Add(txtCuentaDestino);

            Button btnRegistrar = EstiloEurekaBank.CrearBoton("REGISTRAR", 24, 306, 190, 44);
            btnRegistrar.Click += BtnRegistrar_Click;
            tarjeta.Controls.Add(btnRegistrar);

            Button btnRegresar = EstiloEurekaBank.CrearBoton("REGRESAR AL MENU", 210, 306, 214, 44, true);
            btnRegresar.Click += BtnRegresar_Click;
            tarjeta.Controls.Add(btnRegresar);

            lblMensaje = EstiloEurekaBank.CrearMensaje(24, 366, 400);
            tarjeta.Controls.Add(lblMensaje);

            Controls.Add(tarjeta);
        }

        private void CmbTipo_SelectedIndexChanged(object sender, EventArgs e)
        {
            TipoMovimientoItem seleccionado = cmbTipo.SelectedItem as TipoMovimientoItem;
            bool esTransferencia = seleccionado != null && seleccionado.Codigo == EurekaBankModel.Transferencia;
            txtCuentaDestino.Enabled = esTransferencia;
            if (!esTransferencia)
            {
                txtCuentaDestino.Text = string.Empty;
            }
        }

        private void BtnRegistrar_Click(object sender, EventArgs e)
        {
            TipoMovimientoItem seleccionado = cmbTipo.SelectedItem as TipoMovimientoItem;
            if (seleccionado == null)
            {
                EstiloEurekaBank.MostrarError(lblMensaje, "Seleccione un tipo de movimiento.");
                return;
            }

            string mensaje;
            bool exito = controlador.RegistrarMovimiento(
                txtCuenta.Text.Trim(),
                txtImporte.Text.Trim(),
                seleccionado.Codigo,
                txtCuentaDestino.Text.Trim(),
                out mensaje);

            if (exito)
            {
                EstiloEurekaBank.MostrarExito(lblMensaje, mensaje);
                txtImporte.Text = string.Empty;
                txtCuentaDestino.Text = string.Empty;
            }
            else
            {
                EstiloEurekaBank.MostrarError(lblMensaje, mensaje);
            }
        }

        private void BtnRegresar_Click(object sender, EventArgs e)
        {
            Close();
        }
    }
}
