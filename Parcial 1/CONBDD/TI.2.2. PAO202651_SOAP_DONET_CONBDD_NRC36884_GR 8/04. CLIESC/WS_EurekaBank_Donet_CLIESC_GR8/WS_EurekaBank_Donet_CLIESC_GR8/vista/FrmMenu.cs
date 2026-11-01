namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.vista
{
    public class FrmMenu : Form
    {
        public FrmMenu()
        {
            EstiloEurekaBank.PrepararFormulario(this, "Menu principal", 460, 480);
            ConstruirVista();
        }

        private void ConstruirVista()
        {
            Controls.Add(EstiloEurekaBank.CrearEncabezado("Menu principal"));

            Panel tarjeta = EstiloEurekaBank.CrearTarjeta(30, 122, 400, 230);
            tarjeta.Controls.Add(EstiloEurekaBank.CrearTitulo("¿Que deseas hacer?", 24, 18));

            Button btnMovimiento = EstiloEurekaBank.CrearBoton("REGISTRAR MOVIMIENTO", 24, 66, 340, 52);
            btnMovimiento.Click += BtnMovimiento_Click;
            tarjeta.Controls.Add(btnMovimiento);

            Button btnMovimientos = EstiloEurekaBank.CrearBoton("CONSULTAR MOVIMIENTOS", 24, 134, 340, 52);
            btnMovimientos.Click += BtnMovimientos_Click;
            tarjeta.Controls.Add(btnMovimientos);

            Controls.Add(tarjeta);

            Button btnSalir = EstiloEurekaBank.CrearBoton("CERRAR SESION", 30, 372, 400, 40, true);
            btnSalir.Click += BtnSalir_Click;
            Controls.Add(btnSalir);
        }

        private void BtnMovimiento_Click(object sender, EventArgs e)
        {
            FrmMovimiento movimiento = new FrmMovimiento();
            movimiento.Show();
        }

        private void BtnMovimientos_Click(object sender, EventArgs e)
        {
            FrmMovimientos movimientos = new FrmMovimientos();
            movimientos.Show();
        }

        private void BtnSalir_Click(object sender, EventArgs e)
        {
            Close();
            FrmLogin login = new FrmLogin();
            login.Show();
        }
    }
}
