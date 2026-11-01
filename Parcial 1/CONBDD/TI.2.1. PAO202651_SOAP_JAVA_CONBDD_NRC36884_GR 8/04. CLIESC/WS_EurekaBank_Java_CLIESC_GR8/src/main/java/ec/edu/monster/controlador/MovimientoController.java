package ec.edu.monster.controlador;

import ec.edu.monster.vista.MovimientoView;
import ec.edu.monster.vista.Tema;
import ec.edu.monster.ws.MovimientoModel;
import ec.edu.monster.service.MovimientoService;
import ec.edu.monster.service.CuentaService;
import ec.edu.monster.ws.CuentaModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MovimientoController {

    private MovimientoService movimientoService;
    private CuentaService cuentaService;

    public MovimientoController() {
        this.movimientoService = new MovimientoService();
        this.cuentaService = new CuentaService();
    }

    /**
     * Método para cargar y mostrar los movimientos en la vista proporcionada.
     *
     * @param cuenta          Número de cuenta a buscar.
     * @param movimientoView  Instancia de la vista donde se mostrarán los resultados.
     */
    public void cargarMovimientos(String cuenta, MovimientoView movimientoView) {
        try {
            List<MovimientoModel> movimientos = movimientoService.obtenerMovimientos(cuenta);
            CuentaModel cuentaModel = cuentaService.obtenerCuentaPorNumero(cuenta);

            JPanel panelResultados = new JPanel();
            panelResultados.setLayout(new BoxLayout(panelResultados, BoxLayout.Y_AXIS));
            panelResultados.setBackground(Tema.FONDO);

            if (movimientos == null || movimientos.isEmpty()) {
                JLabel noResultados = new JLabel("No se encontraron movimientos para la cuenta: " + cuenta);
                noResultados.setFont(Tema.fuente(Font.BOLD, 16));
                noResultados.setForeground(Tema.ERROR);
                noResultados.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                panelResultados.add(noResultados);
            } else {
                // Crear celdas para cada movimiento
                for (MovimientoModel mov : movimientos) {

                    String saldoTexto;
                    if (mov.getSaldo() != 0) {
                        // Asumimos que el backend ya envía el saldo acumulativo en cada movimiento
                        saldoTexto = "Saldo después: $" + String.format("%.2f", mov.getSaldo());
                    } else {
                        // Si por alguna razón viene 0, lo mostramos como "no disponible"
                        saldoTexto = "Saldo después: N/D";
                    }

                    JPanel celda = movimientoView.crearCelda(
                            "Cuenta: " + mov.getCodigoCuenta(),
                            "Fecha: " + mov.getFechaMovimiento(),
                            "Movimiento: " + mov.getNumeroMovimiento(),
                            "Descripción: " + mov.getTipoDescripcion(),
                            "Tipo Mov.: " + mov.getCodigoTipoMovimiento(),
                            "Importe: $" + String.format("%.2f", mov.getImporteMovimiento()),
                            saldoTexto
                    );
                    panelResultados.add(celda);
                }
                // Resumen de totales
                double totalIngresos = movimientos.stream()
                        .filter(m -> m.getTipoDescripcion() != null && !m.getTipoDescripcion().equals("Retiro"))
                        .mapToDouble(MovimientoModel::getImporteMovimiento)
                        .sum();

                double totalEgresos = movimientos.stream()
                        .filter(m -> m.getTipoDescripcion() != null && m.getTipoDescripcion().equals("Retiro"))
                        .mapToDouble(MovimientoModel::getImporteMovimiento)
                        .sum();

                JPanel resumenPanel = new JPanel(new GridLayout(3, 2, 10, 5));
                resumenPanel.setBackground(Tema.FONDO);
                resumenPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Tema.TEAL, 1, true),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
           
                JLabel saldoCuentaLabel = new JLabel("Saldo de la Cuenta: $" + (cuentaModel != null ? String.format("%.2f", cuentaModel.getDecCuenSaldo()) : "N/A"));
                saldoCuentaLabel.setFont(Tema.fuente(Font.BOLD, 14));
                saldoCuentaLabel.setForeground(Tema.AZUL_MARINO);
                
                JLabel totalMovimientosLabel = new JLabel("Total Movimientos: " + (cuentaModel != null ? cuentaModel.getIntCuenContMov() : "N/A"));
                totalMovimientosLabel.setFont(Tema.fuente(Font.BOLD, 14));
                totalMovimientosLabel.setForeground(Tema.AZUL_MARINO);
                
                JLabel totalIngresosLabel = new JLabel("Total Ingresos: $" + String.format("%.2f", totalIngresos));
                totalIngresosLabel.setFont(Tema.fuente(Font.PLAIN, 13));
                totalIngresosLabel.setForeground(Tema.EXITO);
                
                JLabel totalEgresosLabel = new JLabel("Total Egresos: $" + String.format("%.2f", Math.abs(totalEgresos)));
                totalEgresosLabel.setFont(Tema.fuente(Font.PLAIN, 13));
                totalEgresosLabel.setForeground(Tema.ERROR);
                
                JLabel saldoNetoLabel = new JLabel("Saldo Neto: $" + String.format("%.2f", totalIngresos + totalEgresos));
                saldoNetoLabel.setFont(Tema.fuente(Font.BOLD, 13));
                saldoNetoLabel.setForeground(Tema.TEAL);
                
                JLabel espacio = new JLabel("");

                resumenPanel.add(saldoCuentaLabel);
                resumenPanel.add(totalMovimientosLabel);
                resumenPanel.add(totalIngresosLabel);
                resumenPanel.add(totalEgresosLabel);
                resumenPanel.add(saldoNetoLabel);
                resumenPanel.add(espacio);

                panelResultados.add(resumenPanel);
            }

            // Reemplazar el contenido del JScrollPane con los nuevos resultados
            movimientoView.getjScrollPane1().setViewportView(panelResultados);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(movimientoView,
                    "Error al obtener los movimientos o datos de la cuenta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

