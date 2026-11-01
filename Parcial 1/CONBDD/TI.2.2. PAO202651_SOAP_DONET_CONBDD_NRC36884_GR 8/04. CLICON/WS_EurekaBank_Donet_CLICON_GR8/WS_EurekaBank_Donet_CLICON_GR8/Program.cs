using System;
using WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.controlador;

namespace WS_EurekaBank_Donet_CLICON_GR8
{
    internal static class Program
    {
        private static void Main(string[] args)
        {
            try
            {
                Controlador controlador = new Controlador();
                controlador.Iniciar();
            }
            catch (Exception ex)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("Error inesperado: " + ex.Message);
                Console.ResetColor();
            }

            Console.WriteLine();
            Console.WriteLine("Presione una tecla para cerrar...");
            Console.ReadKey();
        }
    }
}
