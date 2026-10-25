using System.Globalization;
using System.Text;

namespace WS_ConversionUnidades_Donet_CLICON_GR8.Consola
{
    /// <summary>Utilidades para leer datos y mostrar mensajes en la consola.</summary>
    public static class Entrada
    {
        public static string Texto(string mensaje)
        {
            Console.Write(mensaje);
            return (Console.ReadLine() ?? "").Trim();
        }

        // Lee la contraseña mostrando asteriscos (si la entrada viene redirigida, la lee como texto normal)
        public static string Contrasena(string mensaje)
        {
            Console.Write(mensaje);
            if (Console.IsInputRedirected) return Console.ReadLine() ?? "";

            var sb = new StringBuilder();
            while (true)
            {
                var tecla = Console.ReadKey(intercept: true);
                if (tecla.Key == ConsoleKey.Enter)
                {
                    Console.WriteLine();
                    return sb.ToString();
                }
                if (tecla.Key == ConsoleKey.Backspace)
                {
                    if (sb.Length > 0)
                    {
                        sb.Length--;
                        Console.Write("\b \b");
                    }
                }
                else if (!char.IsControl(tecla.KeyChar))
                {
                    sb.Append(tecla.KeyChar);
                    Console.Write('*');
                }
            }
        }

        /// <summary>Muestra una lista numerada y devuelve el índice elegido (base 0), o -1 si cancela con 0.</summary>
        public static int Opcion(string titulo, IReadOnlyList<string> etiquetas)
        {
            Console.WriteLine();
            Console.WriteLine(titulo);
            for (int i = 0; i < etiquetas.Count; i++)
            {
                Console.WriteLine($"  {i + 1}. {etiquetas[i]}");
            }
            Console.WriteLine("  0. Cancelar");

            while (true)
            {
                var texto = Texto("Elige una opción: ");
                if (int.TryParse(texto, out var n) && n >= 0 && n <= etiquetas.Count)
                {
                    return n - 1;
                }
                Error($"Opción no válida. Ingresa un número entre 0 y {etiquetas.Count}.");
            }
        }

        /// <summary>Pide un número (acepta 1.5 o 1,5). Devuelve null si el usuario lo deja vacío para cancelar.</summary>
        public static double? Numero(string mensaje)
        {
            while (true)
            {
                var texto = Texto(mensaje);
                if (texto.Length == 0) return null;

                if (double.TryParse(texto.Replace(',', '.'), NumberStyles.Float, CultureInfo.InvariantCulture, out var valor)
                    && !double.IsNaN(valor) && !double.IsInfinity(valor))
                {
                    return valor;
                }
                Error("Ingresa un valor numérico válido (o deja vacío para cancelar).");
            }
        }

        public static string Formato(double numero) => numero.ToString("0.##########", CultureInfo.InvariantCulture);

        public static void Error(string mensaje) => Escribir(mensaje, ConsoleColor.Red);
        public static void Exito(string mensaje) => Escribir(mensaje, ConsoleColor.Green);

        private static void Escribir(string mensaje, ConsoleColor color)
        {
            var anterior = Console.ForegroundColor;
            Console.ForegroundColor = color;
            Console.WriteLine(mensaje);
            Console.ForegroundColor = anterior;
        }
    }
}
