using System;
using System.Drawing;
using System.Windows.Forms;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.view
{
    public class FrmDeposito : BaseForm
    {
        private readonly DepositoController controlador = new DepositoController();
        private readonly TextBox txtCuenta;
        private readonly TextBox txtMonto;
        private readonly Label lblMensaje;

        public FrmDeposito() : base("Depósito")
        {
            var tarjeta = NuevaTarjeta(176, 16, 480, 340);

            var titulo = new Label
            {
                Text = "Nuevo depósito",
                Font = new Font("Segoe UI", 15F, FontStyle.Bold),
                ForeColor = Theme.Navy,
                Bounds = new Rectangle(24, 20, 432, 30)
            };
            var detalle = new Label
            {
                Text = "Registre un depósito en la cuenta indicada.",
                Font = new Font("Segoe UI", 9F),
                ForeColor = Theme.Muted,
                Bounds = new Rectangle(24, 52, 432, 20)
            };

            var lblCuenta = Etiqueta("Número de cuenta");
            lblCuenta.Location = new Point(24, 92);

            txtCuenta = Campo();
            txtCuenta.Bounds = new Rectangle(24, 114, 432, 28);

            var lblMonto = Etiqueta("Monto a depositar");
            lblMonto.Location = new Point(24, 156);

            txtMonto = Campo();
            txtMonto.Bounds = new Rectangle(24, 178, 432, 28);

            var boton = new PillButton
            {
                Text = "Registrar depósito",
                Bounds = new Rectangle(24, 228, 432, 44)
            };
            boton.Click += Boton_Click;

            lblMensaje = new Label
            {
                Bounds = new Rectangle(24, 282, 432, 38),
                Font = new Font("Segoe UI", 9F),
                TextAlign = ContentAlignment.MiddleLeft,
                Visible = false
            };

            tarjeta.Controls.Add(titulo);
            tarjeta.Controls.Add(detalle);
            tarjeta.Controls.Add(lblCuenta);
            tarjeta.Controls.Add(txtCuenta);
            tarjeta.Controls.Add(lblMonto);
            tarjeta.Controls.Add(txtMonto);
            tarjeta.Controls.Add(boton);
            tarjeta.Controls.Add(lblMensaje);

            AcceptButton = boton;
        }

        private void Boton_Click(object sender, EventArgs e)
        {
            lblMensaje.Visible = false;

            var resultado = controlador.Depositar(txtCuenta.Text, txtMonto.Text);

            if (resultado.Exito)
            {
                MostrarOk(lblMensaje, resultado.Mensaje + "  ·  Saldo: S/ " + resultado.Saldo.ToString("N2"));
                txtCuenta.Clear();
                txtMonto.Clear();
            }
            else
            {
                MostrarError(lblMensaje, resultado.Mensaje);
            }
        }
    }
}
