using System;
using System.Drawing;
using System.Windows.Forms;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.view
{
    public class FrmTransferencia : BaseForm
    {
        private readonly TransferenciaController controlador = new TransferenciaController();
        private readonly TextBox txtCuentaOrigen;
        private readonly TextBox txtCuentaDestino;
        private readonly TextBox txtMonto;
        private readonly Label lblMensaje;

        public FrmTransferencia() : base("Transferencia")
        {
            var tarjeta = NuevaTarjeta(176, 8, 480, 430);

            var titulo = new Label
            {
                Text = "Nueva transferencia",
                Font = new Font("Segoe UI", 15F, FontStyle.Bold),
                ForeColor = Theme.Navy,
                Bounds = new Rectangle(24, 18, 432, 30)
            };
            var detalle = new Label
            {
                Text = "Transfiera un monto entre dos cuentas.",
                Font = new Font("Segoe UI", 9F),
                ForeColor = Theme.Muted,
                Bounds = new Rectangle(24, 50, 432, 20)
            };

            var lblOrigen = Etiqueta("Cuenta origen");
            lblOrigen.Location = new Point(24, 86);

            txtCuentaOrigen = Campo();
            txtCuentaOrigen.Bounds = new Rectangle(24, 108, 432, 28);

            var lblDestino = Etiqueta("Cuenta destino");
            lblDestino.Location = new Point(24, 148);

            txtCuentaDestino = Campo();
            txtCuentaDestino.Bounds = new Rectangle(24, 170, 432, 28);

            var lblMonto = Etiqueta("Monto a transferir");
            lblMonto.Location = new Point(24, 210);

            txtMonto = Campo();
            txtMonto.Bounds = new Rectangle(24, 232, 432, 28);

            var boton = new PillButton
            {
                Text = "Registrar transferencia",
                Bounds = new Rectangle(24, 280, 432, 44)
            };
            boton.Click += Boton_Click;

            lblMensaje = new Label
            {
                Bounds = new Rectangle(24, 334, 432, 38),
                Font = new Font("Segoe UI", 9F),
                TextAlign = ContentAlignment.MiddleLeft,
                Visible = false
            };

            tarjeta.Controls.Add(titulo);
            tarjeta.Controls.Add(detalle);
            tarjeta.Controls.Add(lblOrigen);
            tarjeta.Controls.Add(txtCuentaOrigen);
            tarjeta.Controls.Add(lblDestino);
            tarjeta.Controls.Add(txtCuentaDestino);
            tarjeta.Controls.Add(lblMonto);
            tarjeta.Controls.Add(txtMonto);
            tarjeta.Controls.Add(boton);
            tarjeta.Controls.Add(lblMensaje);

            AcceptButton = boton;
        }

        private void Boton_Click(object sender, EventArgs e)
        {
            lblMensaje.Visible = false;

            var resultado = controlador.Transferir(txtCuentaOrigen.Text, txtCuentaDestino.Text, txtMonto.Text);

            if (resultado.Exito)
            {
                MostrarOk(lblMensaje, resultado.Mensaje + "  ·  Saldo: S/ " + resultado.Saldo.ToString("N2"));
                txtCuentaOrigen.Clear();
                txtCuentaDestino.Clear();
                txtMonto.Clear();
            }
            else
            {
                MostrarError(lblMensaje, resultado.Mensaje);
            }
        }
    }
}
