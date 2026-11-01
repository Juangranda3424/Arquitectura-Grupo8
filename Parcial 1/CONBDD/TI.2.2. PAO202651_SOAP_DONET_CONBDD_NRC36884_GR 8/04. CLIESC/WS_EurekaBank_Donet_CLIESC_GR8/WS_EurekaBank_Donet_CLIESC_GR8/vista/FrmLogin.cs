using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controlador;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.modelo;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.vista
{
    public class FrmLogin : Form
    {
        private readonly EurekaBankController controlador = new EurekaBankController();
        private TextBox txtUsuario;
        private TextBox txtContrasena;
        private Label lblMensaje;

        public FrmLogin()
        {
            EstiloEurekaBank.PrepararFormulario(this, "Iniciar sesion", 460, 560);
            ConstruirVista();
        }

        private void ConstruirVista()
        {
            Controls.Add(EstiloEurekaBank.CrearEncabezado("Iniciar sesion"));

            Panel tarjeta = EstiloEurekaBank.CrearTarjeta(30, 122, 400, 320);
            tarjeta.Controls.Add(EstiloEurekaBank.CrearTitulo("Bienvenido de nuevo", 24, 18));

            Label nota = new Label();
            nota.Text = "Ingresa tus credenciales para operar en EurekaBank.";
            nota.Font = EstiloEurekaBank.Fuente(9F);
            nota.ForeColor = EstiloEurekaBank.Atenuado;
            nota.AutoSize = true;
            nota.Location = new Point(24, 50);
            tarjeta.Controls.Add(nota);

            tarjeta.Controls.Add(EstiloEurekaBank.CrearEtiqueta("USUARIO", 24, 92));
            txtUsuario = EstiloEurekaBank.CrearCampo(24, 112, 340);
            tarjeta.Controls.Add(txtUsuario);

            tarjeta.Controls.Add(EstiloEurekaBank.CrearEtiqueta("CONTRASEÑA", 24, 156));
            txtContrasena = EstiloEurekaBank.CrearCampo(24, 176, 340, true);
            tarjeta.Controls.Add(txtContrasena);

            Button btnIngresar = EstiloEurekaBank.CrearBoton("INGRESAR", 24, 224, 340, 44);
            btnIngresar.Click += BtnIngresar_Click;
            tarjeta.Controls.Add(btnIngresar);

            lblMensaje = EstiloEurekaBank.CrearMensaje(24, 274, 340);
            tarjeta.Controls.Add(lblMensaje);

            Controls.Add(tarjeta);

            Label pie = new Label();
            pie.Text = "TI.2.2 · SOAP .NET · WS_EurekaBank_Donet_GR8";
            pie.Font = EstiloEurekaBank.Fuente(8.5F);
            pie.ForeColor = EstiloEurekaBank.Atenuado;
            pie.AutoSize = true;
            pie.Location = new Point(30, 468);
            Controls.Add(pie);

            Label credenciales = new Label();
            credenciales.Text = "Credenciales de prueba: MONSTER / MONSTER9";
            credenciales.Font = EstiloEurekaBank.Fuente(8.5F);
            credenciales.ForeColor = EstiloEurekaBank.Atenuado;
            credenciales.AutoSize = true;
            credenciales.Location = new Point(30, 492);
            Controls.Add(credenciales);

            AcceptButton = btnIngresar;
        }

        private void BtnIngresar_Click(object sender, EventArgs e)
        {
            string mensaje;
            bool exito = controlador.IniciarSesion(txtUsuario.Text.Trim(), txtContrasena.Text, out mensaje);

            if (!exito)
            {
                EstiloEurekaBank.MostrarError(lblMensaje, mensaje);
                return;
            }

            EstiloEurekaBank.MostrarExito(lblMensaje, mensaje);
            Hide();

            FrmMenu menu = new FrmMenu();
            menu.Show();
        }
    }
}
