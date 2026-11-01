package ec.edu.monster.controller;

import ec.edu.monster.service.CuentaService;
import ec.edu.monster.view.Tema;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class CuentaController {

    private CuentaService cuentaService;

    public CuentaController() {
        this.cuentaService = new CuentaService();
    }

    public void procesarDeposito(String cuenta, String monto, String tipo, String cd) {

        // Validaciones de campos vacíos
        if (cuenta.isEmpty() || monto.isEmpty()) {
            mostrarMensaje("Por favor, complete todos los campos.", "Campos Vacíos");
            return;
        }

        // Validar si el monto es numérico
        if (!esNumeroValido(monto)) {
            mostrarMensaje("El monto debe ser un número válido.", "Error en el Monto");
            return;
        }

        // Realizar depósito
        boolean exito = cuentaService.realizarDeposito(cuenta, monto, tipo, cd);
        if (exito) {
            mostrarMensaje("Proceso realizado exitosamente.", "Éxito");
        } else {
            mostrarMensaje("No se pudo realizar el proceso. Verifique los datos ingresados.", "Error en el Depósito");
        }
    }

    private void mostrarMensaje(String mensaje, String titulo) {
        // Diálogos con la identidad visual de EurekaBank · GR8
        UIManager.put("OptionPane.background", Tema.SUPERFICIE);
        UIManager.put("Panel.background", Tema.SUPERFICIE);
        UIManager.put("OptionPane.messageForeground", Tema.TEXTO);
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean esNumeroValido(String monto) {
        try {
            double valor = Double.parseDouble(monto);
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
