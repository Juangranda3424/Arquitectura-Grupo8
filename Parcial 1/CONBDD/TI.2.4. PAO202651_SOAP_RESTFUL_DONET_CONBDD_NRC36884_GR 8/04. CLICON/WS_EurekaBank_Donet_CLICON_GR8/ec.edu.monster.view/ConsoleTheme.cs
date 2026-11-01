using System;
using System.Runtime.InteropServices;
using System.Text;

namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.view
{
    /// <summary>
    /// Identidad visual compartida (S8) para el cliente de consola:
    /// banner ASCII "EUREKABANK · GR8", cajas de menú y colores ANSI de la paleta.
    /// </summary>
    internal static class ConsoleTheme
    {
        // Paleta S8
        private static readonly string Teal = "\u001b[38;2;15;163;177m";
        private static readonly string Amber = "\u001b[38;2;242;165;65m";
        private static readonly string Muted = "\u001b[38;2;100;116;139m";
        private static readonly string Text = "\u001b[38;2;30;41;59m";
        private static readonly string Success = "\u001b[38;2;22;163;74m";
        private static readonly string Error = "\u001b[38;2;220;38;38m";
        private static readonly string Reset = "\u001b[0m";

        private static bool colorHabilitado;

        private const int Ancho = 58;

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern IntPtr GetStdHandle(int nStdHandle);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern bool GetConsoleMode(IntPtr hConsoleHandle, out uint lpMode);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern bool SetConsoleMode(IntPtr hConsoleHandle, uint dwMode);

        private const int StdOutputHandle = -11;
        private const uint EnableVirtualTerminalProcessing = 0x0004;

        static ConsoleTheme()
        {
            try
            {
                Console.OutputEncoding = new UTF8Encoding(false);
            }
            catch
            {
                // consola sin soporte de UTF-8: se sigue con la codificación actual
            }

            colorHabilitado = HabilitarAnsi();
        }

        private static bool HabilitarAnsi()
        {
            try
            {
                IntPtr handle = GetStdHandle(StdOutputHandle);
                uint mode;
                if (handle == IntPtr.Zero || !GetConsoleMode(handle, out mode))
                {
                    return false;
                }
                return SetConsoleMode(handle, mode | EnableVirtualTerminalProcessing);
            }
            catch
            {
                return false;
            }
        }

        private static string C(string color, string texto)
        {
            return colorHabilitado ? color + texto + Reset : texto;
        }

        // ---- Colores por rol ----

        public static string Marca(string texto) { return C(Teal, texto); }
        public static string Acento(string texto) { return C(Amber, texto); }
        public static string Tenue(string texto) { return C(Muted, texto); }
        public static string Normal(string texto) { return C(Text, texto); }
        public static string Ok(string texto) { return C(Success, texto); }
        public static string Mal(string texto) { return C(Error, texto); }

        // ---- Banner / cajas ----

        public static void Banner()
        {
            Limpiar();
            string teal = colorHabilitado ? Teal : string.Empty;
            string reset = colorHabilitado ? Reset : string.Empty;
            string tenue = colorHabilitado ? Muted : string.Empty;

            Console.WriteLine();
            Console.WriteLine(teal + "  ╔══════════════════════════════════════════════════════════╗" + reset);
            Console.WriteLine(teal + "  ║" + reset + "   ███████╗ " + teal + "EUREKABANK" + reset + "  " + tenue + "·" + reset + "  " + teal + "GR8" + reset + "                " + teal + "║" + reset);
            Console.WriteLine(teal + "  ║" + reset + "   ██╔════╝ Grupo 8 · Arquitectura de Software            " + teal + "║" + reset);
            Console.WriteLine(teal + "  ║" + reset + "   █████╗   TI.2.4 · SOAP RESTFUL .NET (CONBDD)             " + teal + "║" + reset);
            Console.WriteLine(teal + "  ║" + reset + "   ██╔══╝   Cliente de consola                              " + teal + "║" + reset);
            Console.WriteLine(teal + "  ║" + reset + "   ███████╗                                                  " + teal + "║" + reset);
            Console.WriteLine(teal + "  ║" + reset + "   ╚══════╝                                                  " + teal + "║" + reset);
            Console.WriteLine(teal + "  ╚══════════════════════════════════════════════════════════╝" + reset);
            Console.WriteLine();
        }

        public static void Limpiar()
        {
            try
            {
                Console.Clear();
            }
            catch
            {
                // sin consola interactiva
            }
        }

        public static void Titulo(string texto)
        {
            Console.WriteLine("  " + Acento("■ ") + Marca(texto.ToUpperInvariant()));
            Console.WriteLine("  " + Tenue(new string('─', Ancho)));
        }

        public static void Subtitulo(string texto)
        {
            Console.WriteLine("  " + Tenue(texto));
        }

        /// <summary>
        /// Dibuja una caja con título y líneas de contenido.
        /// </summary>
        public static void Caja(string titulo, params string[] lineas)
        {
            Console.WriteLine("  " + TealBox("┌" + new string('─', Ancho) + "┐"));
            Console.WriteLine("  " + TealBox("│") + " " + Acento(Pad(titulo, Ancho - 1)) + TealBox("│"));
            Console.WriteLine("  " + TealBox("├" + new string('─', Ancho) + "┤"));
            foreach (string linea in lineas)
            {
                Console.WriteLine("  " + TealBox("│") + " " + Pad(linea, Ancho - 1) + TealBox("│"));
            }
            Console.WriteLine("  " + TealBox("└" + new string('─', Ancho) + "┘"));
        }

        /// <summary>
        /// Menú numerado dentro de una caja.
        /// </summary>
        public static void Menu(string titulo, params string[] opciones)
        {
            Console.WriteLine("  " + TealBox("┌" + new string('─', Ancho) + "┐"));
            Console.WriteLine("  " + TealBox("│") + " " + Acento(Pad(titulo, Ancho - 1)) + TealBox("│"));
            Console.WriteLine("  " + TealBox("├" + new string('─', Ancho) + "┤"));
            for (int i = 0; i < opciones.Length; i++)
            {
                string linea = Acento((i + 1) + ")") + " " + opciones[i];
                Console.WriteLine("  " + TealBox("│") + " " + Pad(linea, Ancho - 1) + TealBox("│"));
            }
            Console.WriteLine("  " + TealBox("└" + new string('─', Ancho) + "┘"));
        }

        public static void Exito(string mensaje)
        {
            Console.WriteLine();
            Console.WriteLine("  " + Ok("✔ ") + Ok(mensaje));
        }

        public static void Aviso(string mensaje)
        {
            Console.WriteLine();
            Console.WriteLine("  " + Mal("✖ ") + Mal(mensaje));
        }

        public static void Nota(string mensaje)
        {
            Console.WriteLine("  " + Tenue("· " + mensaje));
        }

        public static string Preguntar(string etiqueta)
        {
            Console.Write("  " + Marca("› ") + Normal(etiqueta + ": "));
            return Console.ReadLine();
        }

        public static void Esperar()
        {
            Console.WriteLine();
            Console.Write("  " + Tenue("Presione ENTER para continuar..."));
            Console.ReadLine();
        }

        // ---- utilidades ----

        private static string TealBox(string texto)
        {
            return C(Teal, texto);
        }

        /// <summary>
        /// Longitud visible aproximada: no cuenta secuencias ANSI.
        /// </summary>
        private static int LargoVisible(string texto)
        {
            int largo = 0;
            bool enSecuencia = false;
            foreach (char c in texto)
            {
                if (c == '\u001b')
                {
                    enSecuencia = true;
                    continue;
                }
                if (enSecuencia)
                {
                    if (c == 'm')
                    {
                        enSecuencia = false;
                    }
                    continue;
                }
                largo++;
            }
            return largo;
        }

        private static string Pad(string texto, int ancho)
        {
            int largo = LargoVisible(texto);
            if (largo >= ancho)
            {
                return texto;
            }
            return texto + new string(' ', ancho - largo);
        }
    }
}
