package ec.edu.monster.pruebas;

import ec.edu.monster.modelo.UnidadLongitud;
import ec.edu.monster.modelo.UnidadMasa;
import ec.edu.monster.modelo.UnidadTemperatura;

public class PruebaModelos {

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE CONVERSIÓN DE LONGITUD ===");
        // 100 cm a metros -> Esperado: 1.0 m
        System.out.printf("100 cm -> %.2f Metros%n", 
                UnidadLongitud.CENTIMETRO.convertirA(100, UnidadLongitud.METRO));
        
        // 1 milla a yardas -> Esperado: 1760.0 yardas
        System.out.printf("1 Milla -> %.2f Yardas%n", 
                UnidadLongitud.MILLA.convertirA(1, UnidadLongitud.YARDA));
        
        // 10 pies a centímetros -> Esperado: 304.8 cm
        System.out.printf("10 Pies -> %.2f Centímetros%n", 
                UnidadLongitud.PIE.convertirA(10, UnidadLongitud.CENTIMETRO));


        System.out.println("\n=== PRUEBAS DE CONVERSIÓN DE MASA ===");
        // 1 kilogramo a gramos -> Esperado: 1000.0 g
        System.out.printf("1 kg -> %.2f Gramos%n", 
                UnidadMasa.KILOGRAMO.convertirA(1, UnidadMasa.GRAMO));
        
        // 1 quintal a libras -> Esperado: 220.46 lbs
        System.out.printf("1 Quintal -> %.2f Libras%n", 
                UnidadMasa.QUINTAL.convertirA(1, UnidadMasa.LIBRA));
        
        // 16 onzas a libras -> Esperado: 1.0 lb
        System.out.printf("16 Onzas -> %.2f Libras%n", 
                UnidadMasa.ONZA.convertirA(16, UnidadMasa.LIBRA));


        System.out.println("\n=== PRUEBAS DE CONVERSIÓN DE TEMPERATURA ===");
        // 100 °C a Fahrenheit -> Esperado: 212.0 °F
        System.out.printf("100 °C -> %.2f °F%n", 
                UnidadTemperatura.CELSIUS.convertirA(100, UnidadTemperatura.FAHRENHEIT));
        
        // 0 °C a Kelvin -> Esperado: 273.15 K
        System.out.printf("0 °C -> %.2f K%n", 
                UnidadTemperatura.CELSIUS.convertirA(0, UnidadTemperatura.KELVIN));
        
        // 100 °C a Newton -> Esperado: 33.0 °N
        System.out.printf("100 °C -> %.2f °N%n", 
                UnidadTemperatura.CELSIUS.convertirA(100, UnidadTemperatura.NEWTON));
        
        // 80 °Reaumur a Celsius -> Esperado: 100.0 °C
        System.out.printf("80 °Re -> %.2f °C%n", 
                UnidadTemperatura.REAUMUR.convertirA(80, UnidadTemperatura.CELSIUS));
    }
}