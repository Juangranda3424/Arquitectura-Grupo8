using System;
using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.pruebas
{
    public class PruebaModelos
    {
        public static void Main(string[] args)
        {
            Console.WriteLine("=== PRUEBAS DE CONVERSIÓN DE LONGITUD ===");
            // 100 cm a metros -> Esperado: 1.0 m
            Console.WriteLine($"100 cm -> {UnidadLongitud.CENTIMETRO.convertirA(100, UnidadLongitud.METRO):F2} Metros");

            // 1 milla a yardas -> Esperado: 1760.0 yardas
            Console.WriteLine($"1 Milla -> {UnidadLongitud.MILLA.convertirA(1, UnidadLongitud.YARDA):F2} Yardas");

            // 10 pies a centímetros -> Esperado: 304.8 cm
            Console.WriteLine($"10 Pies -> {UnidadLongitud.PIE.convertirA(10, UnidadLongitud.CENTIMETRO):F2} Centímetros");


            Console.WriteLine("\n=== PRUEBAS DE CONVERSIÓN DE MASA ===");
            // 1 kilogramo a gramos -> Esperado: 1000.0 g
            Console.WriteLine($"1 kg -> {UnidadMasa.KILOGRAMO.convertirA(1, UnidadMasa.GRAMO):F2} Gramos");

            // 1 quintal a libras -> Esperado: 220.46 lbs
            Console.WriteLine($"1 Quintal -> {UnidadMasa.QUINTAL.convertirA(1, UnidadMasa.LIBRA):F2} Libras");

            // 16 onzas a libras -> Esperado: 1.0 lb
            Console.WriteLine($"16 Onzas -> {UnidadMasa.ONZA.convertirA(16, UnidadMasa.LIBRA):F2} Libras");


            Console.WriteLine("\n=== PRUEBAS DE CONVERSIÓN DE TEMPERATURA ===");
            // 100 °C a Fahrenheit -> Esperado: 212.0 °F
            Console.WriteLine($"100 °C -> {UnidadTemperatura.CELSIUS.convertirA(100, UnidadTemperatura.FAHRENHEIT):F2} °F");

            // 0 °C a Kelvin -> Esperado: 273.15 K
            Console.WriteLine($"0 °C -> {UnidadTemperatura.CELSIUS.convertirA(0, UnidadTemperatura.KELVIN):F2} K");

            // 100 °C a Newton -> Esperado: 33.0 °N
            Console.WriteLine($"100 °C -> {UnidadTemperatura.CELSIUS.convertirA(100, UnidadTemperatura.NEWTON):F2} °N");

            // 80 °Reaumur a Celsius -> Esperado: 100.0 °C
            Console.WriteLine($"80 °Re -> {UnidadTemperatura.REAUMUR.convertirA(80, UnidadTemperatura.CELSIUS):F2} °C");

            // Pausa para ver la consola si se ejecuta de manera independiente
            Console.WriteLine("\nPresiona cualquier tecla para salir...");
            Console.ReadKey();
        }
    }
}