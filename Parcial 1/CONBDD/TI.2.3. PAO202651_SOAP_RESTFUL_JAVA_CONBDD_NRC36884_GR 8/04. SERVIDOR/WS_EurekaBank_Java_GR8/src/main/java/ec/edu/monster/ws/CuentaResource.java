/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.ws;

import ec.edu.monster.dao.CuentaDAO;
import ec.edu.monster.modelo.CuentaModel;
import ec.edu.monster.servicio.CuentaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.sql.SQLException;

/**
 *
 * @author crist
 */
@Path("cuenta")
@RequestScoped
public class CuentaResource {
    // Clase de Request para Depósito
    public static class DepositoRequest {
        private String cuenta;
        private double monto;

        public DepositoRequest() {
        }

        public String getCuenta() {
            return cuenta;
        }

        public void setCuenta(String cuenta) {
            this.cuenta = cuenta;
        }

        public double getMonto() {
            return monto;
        }

        public void setMonto(double monto) {
            this.monto = monto;
        }
    }

    // Clase de Request para Retiro
    public static class RetiroRequest {
        private String cuenta;
        private double monto;

        public RetiroRequest() {
        }

        public String getCuenta() {
            return cuenta;
        }

        public void setCuenta(String cuenta) {
            this.cuenta = cuenta;
        }

        public double getMonto() {
            return monto;
        }

        public void setMonto(double monto) {
            this.monto = monto;
        }
    }

    // Clase de Request para Transferencia
    public static class TransferenciaRequest {
        private String cuenta;
        private double monto;
        private String cuentaDestino;

        public TransferenciaRequest() {
        }

        public String getCuenta() {
            return cuenta;
        }

        public void setCuenta(String cuenta) {
            this.cuenta = cuenta;
        }

        public double getMonto() {
            return monto;
        }

        public void setMonto(double monto) {
            this.monto = monto;
        }

        public String getCuentaDestino() {
            return cuentaDestino;
        }

        public void setCuentaDestino(String cuentaDestino) {
            this.cuentaDestino = cuentaDestino;
        }
    }

    // Clase de Response común para todas las operaciones
    public static class OperacionResponse {
        private boolean success;
        private double saldo;
        private String mensaje;

        public OperacionResponse() {
        }

        public OperacionResponse(boolean success, double saldo, String mensaje) {
            this.success = success;
            this.saldo = saldo;
            this.mensaje = mensaje;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public double getSaldo() {
            return saldo;
        }

        public void setSaldo(double saldo) {
            this.saldo = saldo;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
    }

    // Endpoint para Depósito
    @POST
    @Path("deposito")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OperacionResponse deposito(DepositoRequest request) throws SQLException {
        CuentaService servicio = new CuentaService();
        boolean success = servicio.actualizarSaldoYRegistrarMovimiento(
                request.getCuenta(),
                Double.toString(request.getMonto()),
                "DEP",
                null
        );

        // Obtener el saldo actual después de la operación
        CuentaDAO cuentaDAO = new CuentaDAO();
        CuentaModel cuenta = cuentaDAO.obtenerCuentaPorCodigo(request.getCuenta());
        double saldo = (cuenta != null) ? cuenta.getDecCuenSaldo() : 0.0;

        String mensaje = success ? "Depósito realizado exitosamente" : "Error al realizar el depósito";

        return new OperacionResponse(success, saldo, mensaje);
    }

    // Endpoint para Retiro
    @POST
    @Path("retiro")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OperacionResponse retiro(RetiroRequest request) throws SQLException {
        CuentaService servicio = new CuentaService();
        boolean success = servicio.actualizarSaldoYRegistrarMovimiento(
                request.getCuenta(),
                Double.toString(request.getMonto()),
                "RET",
                null
        );

        // Obtener el saldo actual después de la operación
        CuentaDAO cuentaDAO = new CuentaDAO();
        CuentaModel cuenta = cuentaDAO.obtenerCuentaPorCodigo(request.getCuenta());
        double saldo = (cuenta != null) ? cuenta.getDecCuenSaldo() : 0.0;

        String mensaje;
        if (success) {
            mensaje = "Retiro realizado exitosamente";
        } else if (cuenta != null) {
            mensaje = "Saldo insuficiente";
        } else {
            mensaje = "Error al realizar el retiro";
        }

        return new OperacionResponse(success, saldo, mensaje);
    }

    // Endpoint para Transferencia
    @POST
    @Path("transferencia")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OperacionResponse transferencia(TransferenciaRequest request) throws SQLException {
        CuentaService servicio = new CuentaService();
        boolean success = servicio.actualizarSaldoYRegistrarMovimiento(
                request.getCuenta(),
                Double.toString(request.getMonto()),
                "TRA",
                request.getCuentaDestino()
        );

        // Obtener el saldo actual después de la operación
        CuentaDAO cuentaDAO = new CuentaDAO();
        CuentaModel cuenta = cuentaDAO.obtenerCuentaPorCodigo(request.getCuenta());
        double saldo = (cuenta != null) ? cuenta.getDecCuenSaldo() : 0.0;

        String mensaje;
        if (success) {
            mensaje = "Transferencia realizada exitosamente";
        } else if (cuenta != null) {
            mensaje = "Saldo insuficiente o cuenta destino inválida";
        } else {
            mensaje = "Error al realizar la transferencia";
        }

        return new OperacionResponse(success, saldo, mensaje);
    }

    // Endpoint para consultar información de cuenta
    @GET
    @Path("{codigoCuenta}")
    @Produces(MediaType.APPLICATION_JSON)
    public CuentaModel obtenerCuenta(@PathParam("codigoCuenta") String codigoCuenta) throws SQLException {
        CuentaService servicio = new CuentaService();
        return servicio.obtenerCuenta(codigoCuenta);
    }
}
