using System;
using System.Windows.Forms;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.view;

namespace WS_EurekaBank_Donet_CLIESC_GR8
{
    internal static class Program
    {
        /// <summary>
        /// Punto de entrada del cliente de escritorio (Windows Forms).
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new FrmLogin());
        }
    }
}
