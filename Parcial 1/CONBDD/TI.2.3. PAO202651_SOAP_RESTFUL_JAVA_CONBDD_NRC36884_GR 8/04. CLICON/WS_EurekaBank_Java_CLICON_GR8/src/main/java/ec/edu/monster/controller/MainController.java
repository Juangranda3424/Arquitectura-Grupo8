package ec.edu.monster.controller;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.model.MovimientoModel;
import ec.edu.monster.service.CuentaService;
import ec.edu.monster.service.LoginService;
import ec.edu.monster.service.MovimientoService;
import ec.edu.monster.util.Consola;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Controlador del cliente de consola. Mantiene la misma lógica de negocio del
 * cliente original; solo cambia la presentación (identidad EurekaBank · GR8).
 */
public class MainController {

    private final LoginService loginService = new LoginService();
    private final CuentaService cuentaService = new CuentaService();
    private final MovimientoService movimientoService = new MovimientoService();

    private String usuarioActual = null;

    public String getUsuarioActual() {
        return usuarioActual;
    }

    public boolean iniciarSesion(String username, String password) {
        try {
            hashPassword(password);
            if (loginService.auth(username, password)) {
                usuarioActual = username;
                return true;
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println(Consola.error("Error en la autenticación: " + e.getMessage()));
        }
        return false;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public boolean realizarDeposito(String cuenta, String monto, String tipo, String cd) {
        return cuentaService.realizarDeposito(cuenta, monto, tipo, cd);
    }

    public boolean realizarRetiro(String cuenta, String monto) {
        return cuentaService.realizarRetiro(cuenta, monto);
    }

    public boolean realizarTransferencia(String cuentaOrigen, String cuentaDestino, String monto) {
        return cuentaService.realizarTransferencia(cuentaOrigen, cuentaDestino, monto);
    }

    public String verMovimientos(String cuenta) {
        try {
            List<MovimientoModel> movimientos = movimientoService.obtenerMovimientos(cuenta);
            CuentaModel cuentaModel = cuentaService.obtenerCuentaPorNumero(cuenta);

            StringBuilder resultado = new StringBuilder();
            if (movimientos == null || movimientos.isEmpty()) {
                resultado.append("No se encontraron movimientos para la cuenta: ").append(cuenta).append('\n');
                return Consola.caja("MOVIMIENTOS · CUENTA " + cuenta, resultado.toString());
            }

            // Saldo al inicio para que no se pierda en listados largos
            if (cuentaModel != null) {
                resultado.append(String.format("Saldo actual de la cuenta: %.2f%n%n", cuentaModel.getDecCuenSaldo()));
            }

            resultado.append(String.format("%-6s %-12s %-7s %-14s %12s %-10s %12s%n",
                    "Nro", "Fecha", "Tipo", "Descripción", "Importe", "Cta.Ref.", "Saldo"));
            resultado.append("-".repeat(81)).append('\n');

            for (MovimientoModel mov : movimientos) {
                resultado.append(String.format("%-6d %-12s %-7s %-14s %12.2f %-10s %12.2f%n",
                        mov.getNumeroMovimiento(),
                        mov.getFechaMovimiento(),
                        mov.getCodigoTipoMovimiento(),
                        mov.getTipoDescripcion(),
                        mov.getImporteMovimiento(),
                        mov.getCuentaReferencia() != null ? mov.getCuentaReferencia() : "N/A",
                        mov.getSaldo()));
            }

            double totalIngresos = movimientos.stream()
                    .filter(m -> !m.getTipoDescripcion().equals("Retiro"))
                    .mapToDouble(MovimientoModel::getImporteMovimiento)
                    .sum();

            double totalEgresos = movimientos.stream()
                    .filter(m -> m.getTipoDescripcion().equals("Retiro"))
                    .mapToDouble(MovimientoModel::getImporteMovimiento)
                    .sum();

            resultado.append("\nRESUMEN\n");
            resultado.append(String.format("Total ingresos: %.2f%n", totalIngresos));
            resultado.append(String.format("Total egresos (retiros): %.2f%n", Math.abs(totalEgresos)));
            resultado.append(String.format("Saldo neto: %.2f%n", totalIngresos + totalEgresos));

            if (cuentaModel != null) {
                resultado.append(String.format("Saldo actual de la cuenta: %.2f%n", cuentaModel.getDecCuenSaldo()));
            }

            return Consola.caja("MOVIMIENTOS · CUENTA " + cuenta, resultado.toString());
        } catch (Exception e) {
            return Consola.error("Error al obtener los movimientos: " + e.getMessage());
        }
    }

    public String verDatosCuenta(String cuenta) {
        try {
            CuentaModel cuentaModel = cuentaService.obtenerCuentaPorNumero(cuenta);

            if (cuentaModel == null) {
                return Consola.error("No se encontraron datos para la cuenta: " + cuenta);
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append(String.format("%-24s %s%n", "Campo", "Valor"));
            resultado.append("-".repeat(48)).append('\n');
            resultado.append(String.format("%-24s %.2f%n", "Saldo", cuentaModel.getDecCuenSaldo()));
            resultado.append(String.format("%-24s %d%n", "Contador de movimientos", cuentaModel.getIntCuenContMov()));

            return Consola.caja("DATOS DE LA CUENTA " + cuenta, resultado.toString());
        } catch (Exception e) {
            return Consola.error("Error al obtener los datos de la cuenta: " + e.getMessage());
        }
    }

    private void hashPassword(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.digest(input.getBytes());
    }
}
