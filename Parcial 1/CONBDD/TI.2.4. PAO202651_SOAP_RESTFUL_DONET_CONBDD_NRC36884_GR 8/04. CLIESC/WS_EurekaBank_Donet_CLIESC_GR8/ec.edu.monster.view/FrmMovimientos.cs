using System;
using System.Drawing;
using System.Windows.Forms;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.view
{
    public class FrmMovimientos : BaseForm
    {
        private readonly controller.MovimientoController controlador = new controller.MovimientoController();
        private readonly TextBox txtCuenta;
        private readonly DataGridView tabla;
        private readonly Label lblMensaje;
        private readonly Label lblSaldo;
        private readonly Label lblIngresos;
        private readonly Label lblEgresos;
        private readonly Label lblNeto;

        public FrmMovimientos() : base("Movimientos")
        {
            var consulta = NuevaTarjeta(0, 0, 832, 84);

            var lblCuenta = Etiqueta("Número de cuenta");
            lblCuenta.Location = new Point(24, 12);

            txtCuenta = Campo();
            txtCuenta.Bounds = new Rectangle(24, 34, 420, 28);

            var boton = new PillButton
            {
                Text = "Consultar movimientos",
                Bounds = new Rectangle(468, 30, 220, 40)
            };
            boton.Click += Boton_Click;

            consulta.Controls.Add(lblCuenta);
            consulta.Controls.Add(txtCuenta);
            consulta.Controls.Add(boton);

            var resumen = NuevaTarjeta(0, 96, 832, 88);

            lblSaldo = CrearEstadistica(resumen, 24, "SALDO ACTUAL");
            lblIngresos = CrearEstadistica(resumen, 224, "INGRESOS");
            lblEgresos = CrearEstadistica(resumen, 424, "EGRESOS");
            lblNeto = CrearEstadistica(resumen, 624, "SALDO NETO");

            tabla = new DataGridView
            {
                Bounds = new Rectangle(24, 220, 832, 232),
                BackgroundColor = Theme.Surface,
                BorderStyle = BorderStyle.None,
                AllowUserToAddRows = false,
                AllowUserToDeleteRows = false,
                AllowUserToResizeRows = false,
                ReadOnly = true,
                RowHeadersVisible = false,
                MultiSelect = false,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                EnableHeadersVisualStyles = false,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                GridColor = Theme.Border
            };

            tabla.ColumnHeadersDefaultCellStyle.BackColor = Theme.Navy;
            tabla.ColumnHeadersDefaultCellStyle.ForeColor = Color.White;
            tabla.ColumnHeadersDefaultCellStyle.Font = new Font("Segoe UI Semibold", 9F, FontStyle.Bold);
            tabla.ColumnHeadersDefaultCellStyle.SelectionBackColor = Theme.Navy;
            tabla.ColumnHeadersHeight = 34;
            tabla.DefaultCellStyle.Font = new Font("Segoe UI", 9F);
            tabla.DefaultCellStyle.ForeColor = Theme.Text;
            tabla.DefaultCellStyle.SelectionBackColor = Color.FromArgb(224, 244, 246);
            tabla.DefaultCellStyle.SelectionForeColor = Theme.Navy;
            tabla.RowTemplate.Height = 26;

            AgregarColumna("Nro", "Nro", 44);
            AgregarColumna("Fecha", "Fecha", 80);
            AgregarColumna("Tipo", "Tipo", 52);
            AgregarColumna("Descripción", "Descripción", 150);
            AgregarColumna("Importe", "Importe", 80);
            AgregarColumna("CtaRef", "Cta. Ref.", 70);
            AgregarColumna("Saldo", "Saldo", 80);

            lblMensaje = new Label
            {
                Bounds = new Rectangle(24, 460, 832, 26),
                Font = new Font("Segoe UI", 9F),
                ForeColor = Theme.Muted,
                TextAlign = ContentAlignment.MiddleLeft
            };

            Contenido.Controls.Add(tabla);
            Contenido.Controls.Add(lblMensaje);

            AcceptButton = boton;
        }

        private static Label CrearEstadistica(Control padre, int x, string titulo)
        {
            var etiqueta = new Label
            {
                Text = titulo,
                Font = new Font("Segoe UI", 7.5F, FontStyle.Bold),
                ForeColor = Theme.Muted,
                Bounds = new Rectangle(x, 16, 180, 16)
            };

            var valor = new Label
            {
                Text = "S/ 0,00",
                Font = new Font("Segoe UI", 14F, FontStyle.Bold),
                ForeColor = Theme.Teal,
                Bounds = new Rectangle(x, 34, 190, 34)
            };

            padre.Controls.Add(etiqueta);
            padre.Controls.Add(valor);
            return valor;
        }

        private void AgregarColumna(string nombre, string titulo, int anchoMinimo)
        {
            var columna = new DataGridViewTextBoxColumn
            {
                Name = nombre,
                HeaderText = titulo,
                MinimumWidth = anchoMinimo,
                SortMode = DataGridViewColumnSortMode.NotSortable
            };
            tabla.Columns.Add(columna);
        }

        private void Boton_Click(object sender, EventArgs e)
        {
            tabla.Rows.Clear();
            lblMensaje.Text = string.Empty;

            try
            {
                var resumen = controlador.Consultar(txtCuenta.Text);

                lblSaldo.Text = "S/ " + resumen.SaldoActual.ToString("N2");
                lblIngresos.Text = "S/ " + resumen.TotalIngresos.ToString("N2");
                lblEgresos.Text = "S/ " + resumen.TotalEgresos.ToString("N2");
                lblNeto.Text = "S/ " + resumen.SaldoNeto.ToString("N2");

                foreach (var mov in resumen.Movimientos)
                {
                    string fecha = mov.FechaMovimientoDt.HasValue
                        ? mov.FechaMovimientoDt.Value.ToString("dd/MM/yyyy")
                        : (mov.FechaMovimiento ?? "N/A");

                    tabla.Rows.Add(
                        mov.NumeroMovimiento,
                        fecha,
                        mov.CodigoTipoMovimiento ?? "N/A",
                        mov.TipoDescripcion ?? "N/A",
                        mov.ImporteMovimiento.ToString("N2"),
                        string.IsNullOrEmpty(mov.CuentaReferencia) ? "—" : mov.CuentaReferencia,
                        mov.Saldo.ToString("N2"));
                }

                if (resumen.Movimientos.Count == 0)
                {
                    lblMensaje.ForeColor = Theme.Muted;
                    lblMensaje.Text = "No se encontraron movimientos para la cuenta " + txtCuenta.Text.Trim() + ".";
                }
                else
                {
                    lblMensaje.ForeColor = Theme.Muted;
                    lblMensaje.Text = resumen.Movimientos.Count + " movimiento(s) encontrados.";
                }
            }
            catch (Exception ex)
            {
                MostrarError(lblMensaje, ex.Message);
                lblSaldo.Text = "S/ 0,00";
                lblIngresos.Text = "S/ 0,00";
                lblEgresos.Text = "S/ 0,00";
                lblNeto.Text = "S/ 0,00";
            }
        }
    }
}
