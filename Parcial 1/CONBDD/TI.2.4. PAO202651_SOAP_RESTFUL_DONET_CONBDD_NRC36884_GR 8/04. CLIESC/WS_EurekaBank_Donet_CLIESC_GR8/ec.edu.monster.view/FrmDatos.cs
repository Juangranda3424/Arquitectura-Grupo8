using System;
using System.Drawing;
using System.Windows.Forms;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.view
{
    public class FrmDatos : BaseForm
    {
        private readonly DatosController controlador = new DatosController();
        private readonly TextBox txtCuenta;
        private readonly Label lblMensaje;
        private readonly Panel panelDatos;

        private readonly Label valorCodigo;
        private readonly Label valorSaldo;
        private readonly Label valorContador;
        private readonly Label valorFecha;
        private readonly Label valorEstado;
        private readonly Label valorMoneda;
        private readonly Label valorSucursal;

        public FrmDatos() : base("Datos de la cuenta")
        {
            var consulta = NuevaTarjeta(0, 0, 832, 84);

            var lblCuenta = Etiqueta("Número de cuenta");
            lblCuenta.Location = new Point(24, 12);

            txtCuenta = Campo();
            txtCuenta.Bounds = new Rectangle(24, 34, 420, 28);

            var boton = new PillButton
            {
                Text = "Consultar cuenta",
                Bounds = new Rectangle(468, 30, 220, 40)
            };
            boton.Click += Boton_Click;

            consulta.Controls.Add(lblCuenta);
            consulta.Controls.Add(txtCuenta);
            consulta.Controls.Add(boton);

            panelDatos = NuevaTarjeta(0, 96, 832, 330);

            var titulo = new Label
            {
                Text = "Información de la cuenta",
                Font = new Font("Segoe UI", 13F, FontStyle.Bold),
                ForeColor = Theme.Navy,
                Bounds = new Rectangle(24, 18, 780, 28)
            };
            panelDatos.Controls.Add(titulo);

            valorCodigo = AgregarFila(24, 62, "Código de cuenta");
            valorSaldo = AgregarFila(24, 122, "Saldo");
            valorContador = AgregarFila(24, 182, "Contador de movimientos");
            valorFecha = AgregarFila(24, 242, "Fecha de creación");

            valorEstado = AgregarFila(432, 62, "Estado");
            valorMoneda = AgregarFila(432, 122, "Moneda");
            valorSucursal = AgregarFila(432, 182, "Sucursal");

            lblMensaje = new Label
            {
                Bounds = new Rectangle(24, 462, 832, 26),
                Font = new Font("Segoe UI", 9F),
                ForeColor = Theme.Muted,
                TextAlign = ContentAlignment.MiddleLeft
            };

            Contenido.Controls.Add(lblMensaje);

            AcceptButton = boton;
            Limpiar();
        }

        private Label AgregarFila(int x, int y, string etiqueta)
        {
            var titulo = new Label
            {
                Text = etiqueta.ToUpperInvariant(),
                Font = new Font("Segoe UI", 7.5F, FontStyle.Bold),
                ForeColor = Theme.Muted,
                Bounds = new Rectangle(x, y, 360, 16)
            };

            var valor = new Label
            {
                Text = "—",
                Font = new Font("Segoe UI", 11F, FontStyle.Regular),
                ForeColor = Theme.Text,
                Bounds = new Rectangle(x, y + 18, 360, 26)
            };

            panelDatos.Controls.Add(titulo);
            panelDatos.Controls.Add(valor);
            return valor;
        }

        private void Limpiar()
        {
            valorCodigo.Text = "—";
            valorSaldo.Text = "—";
            valorContador.Text = "—";
            valorFecha.Text = "—";
            valorEstado.Text = "—";
            valorMoneda.Text = "—";
            valorSucursal.Text = "—";
            lblMensaje.Text = string.Empty;
        }

        private void Boton_Click(object sender, EventArgs e)
        {
            Limpiar();

            try
            {
                var cuenta = controlador.Consultar(txtCuenta.Text);

                if (cuenta == null)
                {
                    MostrarError(lblMensaje, "No se encontraron datos para la cuenta especificada");
                    return;
                }

                valorCodigo.Text = cuenta.ChrCuenCodigo ?? "—";
                valorSaldo.Text = "S/ " + cuenta.DecCuenSaldo.ToString("N2");
                valorSaldo.ForeColor = Theme.Teal;
                valorContador.Text = cuenta.IntCuenContMov.ToString();
                valorFecha.Text = cuenta.DttCuenFechaCreacion.HasValue
                    ? cuenta.DttCuenFechaCreacion.Value.ToString("dd/MM/yyyy")
                    : "—";
                valorEstado.Text = cuenta.VchCuenEstado ?? "—";
                valorMoneda.Text = cuenta.ChrMoneCodigo ?? "—";
                valorSucursal.Text = cuenta.ChrSucucodigo ?? "—";

                MostrarOk(lblMensaje, "Datos obtenidos del servidor.");
            }
            catch (Exception ex)
            {
                MostrarError(lblMensaje, ex.Message);
            }
        }
    }
}
