using System;
using WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.modelo;

namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.vista
{
    /// <summary>
    /// Identidad visual de consola de EurekaBank Grupo 8:
    /// banner ASCII, menus enmarcados y colores ANSI/consola.
    /// </summary>
    public static class Consola
    {
        public static readonly ConsoleColor ColorMarca = ConsoleColor.DarkCyan;
        public static readonly ConsoleColor ColorAcento = ConsoleColor.Yellow;
        public static readonly ConsoleColor ColorTexto = ConsoleColor.Gray;
        public static readonly ConsoleColor ColorExito = ConsoleColor.Green;
        public static readonly ConsoleColor ColorError = ConsoleColor.Red;

        private const string Ancho = "+---------------------------------------------------------+";

        private static readonly string[] Banner =
        {
            "  #####  #   #  ####   ####  #   #   ###   ####   ###   #   #  #   #",
            "  #      #   #  #   #  #     #  #   #   #  #   # #   #  ##  #  #  #",
            "  ####   #   #  ####   ####  ###    #####  ####  #####  # # #  ###",
            "  #      #   #  #  #   #     #  #   #   #  #   # #   #  #  ##  #  #",
            "  #####   ###   #   #  ####  #   #  #   #  ####  #   #  #   #  #   #"
        };

        public static void Inicializar()
        {
            Console.Title = "EurekaBank · Grupo 8 · CLICON";
            Console.OutputEncoding = System.Text.Encoding.UTF8;
            Console.BackgroundColor = ConsoleColor.Black;
            Console.ForegroundColor = ColorTexto;
            Console.Clear();
        }

        public static void BannerBienvenida()
        {
            Console.WriteLine();
            EscribirColor(ColorMarca, Banner);
            Console.WriteLine();
            EscribirColor(ColorAcento, "        EUREKABANK · GR8   |   Grupo 8 · Arquitectura de Software");
            EscribirColor(ConsoleColor.DarkGray, "        Cliente de consola SOAP (.NET Framework) · TI.2.2");
            Console.WriteLine();
        }

        public static void Encabezado(string titulo)
        {
            Console.WriteLine();
            EscribirColor(ColorMarca, Ancho);
            EscribirColor(ColorAcento, "| " + Centrar(titulo, Ancho.Length - 4) + " |");
            EscribirColor(ColorMarca, Ancho);
        }

        public static void MenuPrincipal()
        {
            Encabezado("MENU PRINCIPAL");
            Console.WriteLine("|  [1]  Registrar movimiento                              |");
            Console.WriteLine("|  [2]  Consultar movimientos                             |");
            Console.WriteLine("|  [3]  Salir                                             |");
            EscribirColor(ColorMarca, Ancho);
            Console.Write("  Opcion: ");
        }

        public static void MenuTipoMovimiento()
        {
            Console.WriteLine();
            Console.WriteLine("  Tipo de movimiento:");
            Console.WriteLine("    [1]  Deposito        (codigo 003)");
            Console.WriteLine("    [2]  Retiro          (codigo 004)");
            Console.WriteLine("    [3]  Transferencia   (codigo 008)");
            Console.Write("  Opcion: ");
        }

        public static void Ok(string mensaje)
        {
            EscribirColor(ColorExito, "  [OK] " + mensaje);
        }

        public static void Error(string mensaje)
        {
            EscribirColor(ColorError, "  [X]  " + mensaje);
        }

        public static void Info(string mensaje)
        {
            EscribirColor(ColorAcento, "  [i]  " + mensaje);
        }

        public static void Etiqueta(string texto)
        {
            EscribirColor(ConsoleColor.Cyan, "  " + texto);
        }

        public static void TablaMovimientos(System.Collections.Generic.IEnumerable<Movimiento> movimientos)
        {
            Console.WriteLine();
            EscribirColor(ColorMarca, "+--------+------------+---------------+-----------+-------------------------+");
            EscribirColor(ColorAcento, "| Nro.Mv |   Fecha    |     Tipo      |   Accion  |        Importe          |");
            EscribirColor(ColorMarca, "+--------+------------+---------------+-----------+-------------------------+");

            foreach (Movimiento movimiento in movimientos)
            {
                Console.WriteLine(string.Format(
                    "| {0,6} | {1:yyyy-MM-dd} | {2,-13} | {3,-9} | {4,23:N2} |",
                    movimiento.NroMov,
                    movimiento.Fecha,
                    Recortar(movimiento.Tipo, 13),
                    Recortar(movimiento.Accion, 9),
                    movimiento.Importe));
            }

            EscribirColor(ColorMarca, "+--------+------------+---------------+-----------+-------------------------+");
        }

        private static string Recortar(string valor, int largo)
        {
            if (string.IsNullOrEmpty(valor))
            {
                return string.Empty.PadRight(largo);
            }

            return valor.Length <= largo ? valor : valor.Substring(0, largo);
        }

        private static string Centrar(string texto, int ancho)
        {
            if (texto.Length >= ancho)
            {
                return texto.Substring(0, ancho);
            }

            int izquierda = (ancho - texto.Length) / 2;
            return new string(' ', izquierda) + texto + new string(' ', ancho - texto.Length - izquierda);
        }

        private static void EscribirColor(ConsoleColor color, params string[] lineas)
        {
            ConsoleColor anterior = Console.ForegroundColor;
            Console.ForegroundColor = color;
            foreach (string linea in lineas)
            {
                Console.WriteLine(linea);
            }

            Console.ForegroundColor = anterior;
        }

        private static void EscribirColor(ConsoleColor color, string linea)
        {
            EscribirColor(color, new[] { linea });
        }
    }
}
