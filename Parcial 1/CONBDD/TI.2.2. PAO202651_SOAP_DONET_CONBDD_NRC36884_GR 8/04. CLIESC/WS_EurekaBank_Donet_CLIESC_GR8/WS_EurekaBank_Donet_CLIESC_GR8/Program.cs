using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.vista;

namespace WS_EurekaBank_Donet_CLIESC_GR8
{
    internal static class Program
    {
        [STAThread]
        private static void Main()
        {
            ApplicationConfiguration.Initialize();
            Application.Run(new FrmLogin());
        }
    }
}
