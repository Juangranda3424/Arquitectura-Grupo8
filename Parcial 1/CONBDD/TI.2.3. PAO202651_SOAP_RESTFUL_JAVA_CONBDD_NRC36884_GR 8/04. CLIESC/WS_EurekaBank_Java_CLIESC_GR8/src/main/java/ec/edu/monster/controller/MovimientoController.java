package ec.edu.monster.controller;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.model.MovimientoModel;
import ec.edu.monster.service.CuentaService;
import ec.edu.monster.service.MovimientoService;
import ec.edu.monster.view.MovimientoView;
import ec.edu.monster.view.Tema;

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
     * Carga y muestra los movimientos en la vista indicada.
     *
     * @param cuenta         número de cuenta a consultar.
     * @param movimientoView vista donde se muestran los resultados.
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
                noResultados.setFont(Tema.fuente(Font.BOLD, 14));
                noResultados.setForeground(Tema.ERROR);
                panelResultados.add(noResultados);
            } else {
                for (MovimientoModel mov : movimientos) {
                    String saldoTexto;
                    if (mov.getSaldo() != 0) {
                        saldoTexto = "Saldo después: $" + String.format("%.2f", mov.getSaldo());
                    } else {
                        saldoTexto = "Saldo después: N/D";
                    }

                    JPanel celda = movimientoView.crearCelda(
                            "Cuenta: " + mov.getCodigoCuenta(),
                            "Fecha: " + mov.getFechaMovimiento(),
                            "Movimiento: " + mov.getNumeroMovimiento(),
                            "Descripción: " + mov.getTipoDescripcion(),
                            "Tipo mov.: " + mov.getCodigoTipoMovimiento(),
                            "Importe: $" + String.format("%.2f", mov.getImporteMovimiento()),
                            saldoTexto
                    );
                    panelResultados.add(celda);
                }

                JPanel resumenPanel = new JPanel(new GridLayout(2, 1, 0, 4));
                resumenPanel.setBackground(Tema.FONDO);
                resumenPanel.setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));

                JLabel saldoCuentaLabel = new JLabel("Saldo de la cuenta: $"
                        + (cuentaModel != null ? String.format("%.2f", cuentaModel.getDecCuenSaldo()) : "N/A"));
                saldoCuentaLabel.setFont(Tema.fuente(Font.BOLD, 13));
                saldoCuentaLabel.setForeground(Tema.NAVY);

                JLabel totalMovimientosLabel = new JLabel("Total movimientos: "
                        + (cuentaModel != null ? cuentaModel.getIntCuenContMov() : "N/A"));
                totalMovimientosLabel.setFont(Tema.fuente(Font.PLAIN, 13));
                totalMovimientosLabel.setForeground(Tema.MUTED);

                resumenPanel.add(saldoCuentaLabel);
                resumenPanel.add(totalMovimientosLabel);

                panelResultados.add(resumenPanel);
            }

            movimientoView.getjScrollPane1().setViewportView(panelResultados);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(movimientoView,
                    "Error al obtener los movimientos o datos de la cuenta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
