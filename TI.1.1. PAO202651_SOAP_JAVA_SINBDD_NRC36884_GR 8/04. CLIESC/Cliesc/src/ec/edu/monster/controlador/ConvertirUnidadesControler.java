/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.servicio.ConversionUnidadesServicio;

/**
 *
 * @author JUAN
 */
public class ConvertirUnidadesControler {

    public double convertirUnidades(double num1, double num2) {
        ConversionUnidadesServicio servicio = new ConversionUnidadesServicio();
        return servicio.convertirUnidades(num1, num2);
    }
    
}
