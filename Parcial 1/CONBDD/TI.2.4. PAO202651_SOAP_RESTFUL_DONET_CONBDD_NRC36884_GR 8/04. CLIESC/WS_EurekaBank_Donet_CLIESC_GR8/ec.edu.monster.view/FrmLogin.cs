using System;
using System.Drawing;
using System.Windows.Forms;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.model;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.view
{
    public class FrmLogin : BaseForm
    {
        private readonly LoginController controlador = new LoginController();
        private readonly TextBox txtUsuario;
        private readonly TextBox txtContrasena;
        private readonly Label lblMensaje;

        public FrmLogin() : base("Iniciar sesión")
        {
            ClientSize = new Size(880, 620);

            var tarjeta = NuevaTarjeta(216, 24, 400, 430);

            var logo = new LogoControl { Location = new Point(168, 28), Size = new Size(64, 64) };
            var titulo = new Label
            {
                Text = "EurekaBank",
                Font = Theme.FuenteTitulo,
                ForeColor = Theme.Navy,
                TextAlign = ContentAlignment.MiddleCenter,
                Bounds = new Rectangle(0, 104, 400, 36)
            };
            var subtitulo = new Label
            {
                Text = "Grupo 8 · Arquitectura de Software",
                Font = Theme.FuenteSubtitulo,
                ForeColor = Theme.Muted,
                TextAlign = ContentAlignment.MiddleCenter,
                Bounds = new Rectangle(0, 140, 400, 22)
            };
            var separador = new Panel
            {
                BackColor = Theme.Border,
                Bounds = new Rectangle(48, 178, 304, 1)
            };

            var lblUsuario = Etiqueta("Usuario");
            lblUsuario.Location = new Point(48, 198);

            txtUsuario = Campo();
            txtUsuario.Bounds = new Rectangle(48, 220, 304, 30);

            var lblContrasena = Etiqueta("Contraseña");
            lblContrasena.Location = new Point(48, 264);

            txtContrasena = Campo(true);
            txtContrasena.Bounds = new Rectangle(48, 286, 304, 30);

            var boton = new PillButton
            {
                Text = "Iniciar sesión",
                Bounds = new Rectangle(48, 340, 304, 44)
            };
            boton.Click += Boton_Click;

            lblMensaje = new Label
            {
                Bounds = new Rectangle(48, 392, 304, 34),
                Font = new Font("Segoe UI", 8.5F),
                TextAlign = ContentAlignment.MiddleCenter,
                Visible = false
            };

            tarjeta.Controls.Add(logo);
            tarjeta.Controls.Add(titulo);
            tarjeta.Controls.Add(subtitulo);
            tarjeta.Controls.Add(separador);
            tarjeta.Controls.Add(lblUsuario);
            tarjeta.Controls.Add(txtUsuario);
            tarjeta.Controls.Add(lblContrasena);
            tarjeta.Controls.Add(txtContrasena);
            tarjeta.Controls.Add(boton);
            tarjeta.Controls.Add(lblMensaje);

            AcceptButton = boton;
            txtUsuario.Focus();
        }

        private void Boton_Click(object sender, EventArgs e)
        {
            var resultado = controlador.IniciarSesion(txtUsuario.Text, txtContrasena.Text);

            if (resultado.Exito)
            {
                MostrarOk(lblMensaje, "Bienvenido, " + txtUsuario.Text.Trim());
                Navegar(new FrmMenu(txtUsuario.Text.Trim()));
            }
            else
            {
                MostrarError(lblMensaje, resultado.Mensaje);
                txtContrasena.Clear();
                txtContrasena.Focus();
            }
        }
    }
}
