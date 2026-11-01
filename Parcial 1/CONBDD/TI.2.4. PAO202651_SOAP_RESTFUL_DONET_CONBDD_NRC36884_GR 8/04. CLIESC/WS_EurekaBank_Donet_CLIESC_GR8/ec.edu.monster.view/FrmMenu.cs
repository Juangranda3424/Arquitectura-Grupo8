using System;
using System.Drawing;
using System.Windows.Forms;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.view
{
    public class FrmMenu : BaseForm
    {
        private readonly string usuario;

        public FrmMenu(string usuario) : base("Menú principal")
        {
            this.usuario = string.IsNullOrWhiteSpace(usuario) ? "MONSTER" : usuario;

            ConstruirBienvenida();
            ConstruirOpciones();
        }

        private void ConstruirBienvenida()
        {
            var tarjeta = NuevaTarjeta(0, 0, 832, 104);

            var titulo = new Label
            {
                Text = "Bienvenido, " + usuario.ToUpperInvariant(),
                Font = new Font("Segoe UI", 17F, FontStyle.Bold),
                ForeColor = Theme.Navy,
                Bounds = new Rectangle(24, 22, 560, 34)
            };

            var subtitulo = new Label
            {
                Text = "Seleccione una operación para continuar",
                Font = Theme.FuenteSubtitulo,
                ForeColor = Theme.Muted,
                Bounds = new Rectangle(26, 58, 560, 22)
            };

            var cerrarSesion = new PillButton
            {
                Text = "Cerrar sesión",
                BaseColor = Theme.NavyLight,
                HoverColor = Theme.Navy,
                Bounds = new Rectangle(648, 34, 160, 38)
            };
            cerrarSesion.Click += (s, e) => Close();

            tarjeta.Controls.Add(titulo);
            tarjeta.Controls.Add(subtitulo);
            tarjeta.Controls.Add(cerrarSesion);
        }

        private void ConstruirOpciones()
        {
            var opciones = new[]
            {
                new { Titulo = "Depósito", Detalle = "Registrar un depósito en una cuenta", Form = (Func<Form>)(() => new FrmDeposito()) },
                new { Titulo = "Retiro", Detalle = "Registrar un retiro de una cuenta", Form = (Func<Form>)(() => new FrmRetiro()) },
                new { Titulo = "Transferencia", Detalle = "Transferir entre dos cuentas", Form = (Func<Form>)(() => new FrmTransferencia()) },
                new { Titulo = "Movimientos", Detalle = "Ver historial y totales de la cuenta", Form = (Func<Form>)(() => new FrmMovimientos()) },
                new { Titulo = "Datos de cuenta", Detalle = "Consultar la información de la cuenta", Form = (Func<Form>)(() => new FrmDatos()) }
            };

            int ancho = 264;
            int alto = 132;
            int separacion = 20;

            for (int i = 0; i < opciones.Length; i++)
            {
                int columna = i % 3;
                int fila = i / 3;
                int x = columna * (ancho + separacion);
                int y = 128 + fila * (alto + separacion);

                var opcion = opciones[i];
                var tarjeta = NuevaTarjeta(x, y, ancho, alto);

                var acento = new Panel
                {
                    BackColor = Theme.Teal,
                    Bounds = new Rectangle(0, 24, 5, 84)
                };
                var titulo = new Label
                {
                    Text = opcion.Titulo,
                    Font = new Font("Segoe UI Semibold", 12.5F, FontStyle.Bold),
                    ForeColor = Theme.Navy,
                    Bounds = new Rectangle(24, 26, ancho - 48, 26)
                };
                var detalle = new Label
                {
                    Text = opcion.Detalle,
                    Font = new Font("Segoe UI", 9F),
                    ForeColor = Theme.Muted,
                    Bounds = new Rectangle(24, 56, ancho - 48, 40)
                };
                var accion = new Label
                {
                    Text = "Abrir  →",
                    Font = new Font("Segoe UI Semibold", 9F, FontStyle.Bold),
                    ForeColor = Theme.Amber,
                    Bounds = new Rectangle(24, 96, ancho - 48, 20)
                };

                tarjeta.Controls.Add(acento);
                tarjeta.Controls.Add(titulo);
                tarjeta.Controls.Add(detalle);
                tarjeta.Controls.Add(accion);

                Action abrir = () =>
                {
                    using (var formulario = opcion.Form())
                    {
                        formulario.StartPosition = FormStartPosition.CenterParent;
                        formulario.ShowDialog(this);
                    }
                };

                tarjeta.Click += (s, e) => abrir();
                acento.Click += (s, e) => abrir();
                titulo.Click += (s, e) => abrir();
                detalle.Click += (s, e) => abrir();
                accion.Click += (s, e) => abrir();
                tarjeta.Cursor = Cursors.Hand;
            }

            var pie = new Label
            {
                Text = "API: " + ApiClient.BaseUrl,
                Font = new Font("Segoe UI", 8.5F),
                ForeColor = Theme.Muted,
                Bounds = new Rectangle(50, 452, 808, 20)
            };
            Contenido.Controls.Add(pie);
        }
    }
}
