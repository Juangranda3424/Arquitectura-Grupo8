/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.pruebas;

import ec.edu.monster.servicio.ConversionUnidadesServicio;

/**
 *
 * @author JUAN
 */
public class ConversionUnidadesPrueba {
    
    public static void main(String[] args) {
        
        //Datos
        double num1 = 10;
        double num2 = 10;
        
        //Proceso
        ConversionUnidadesServicio servicio = new ConversionUnidadesServicio();
        
        //Resultado
        if(servicio.convertirUnidades(num1, num2) == 20.0) {
            
            System.out.println("Resultado esperado 20");
            System.out.println("Numero 1: " + num1);
            System.out.println("Numero 2: " + num2);
            
            System.out.println("Resultado: " + servicio.convertirUnidades(num1, num2));

        }
        
        
    }
    
}
