package ec.edu.espe.banquito.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.espe.banquito.config.CreditoProperties;
import ec.edu.espe.banquito.dto.CreditoResponse;
import ec.edu.espe.banquito.dto.MontoMaximoResponse;
import ec.edu.espe.banquito.dto.Motivo;
import ec.edu.espe.banquito.dto.OtorgarCreditoRequest;
import ec.edu.espe.banquito.dto.SujetoCreditoResponse;
import ec.edu.espe.banquito.exception.CreditoRechazadoException;
import ec.edu.espe.banquito.exception.RecursoNoEncontradoException;
import ec.edu.espe.banquito.exception.SolicitudInvalidaException;
import ec.edu.espe.banquito.model.Cliente;
import ec.edu.espe.banquito.model.Credito;
import ec.edu.espe.banquito.model.Movimiento;
import ec.edu.espe.banquito.model.TablaAmortizacion;
import ec.edu.espe.banquito.repository.ClienteRepository;
import ec.edu.espe.banquito.repository.CreditoRepository;
import ec.edu.espe.banquito.repository.MovimientoRepository;
import ec.edu.espe.banquito.service.AmortizacionCalculator.PlanPagos;

@Service
public class CreditoService {

    private static final int EDAD_MINIMA_MASCULINO = 25;

    private final ClienteRepository clientes;
    private final MovimientoRepository movimientos;
    private final CreditoRepository creditos;
    private final AmortizacionCalculator calculator;
    private final CreditoProperties props;
    private final Clock clock;

    public CreditoService(ClienteRepository clientes, MovimientoRepository movimientos, CreditoRepository creditos,
            AmortizacionCalculator calculator, CreditoProperties props, Clock clock) {
        this.clientes = clientes;
        this.movimientos = movimientos;
        this.creditos = creditos;
        this.calculator = calculator;
        this.props = props;
        this.clock = clock;
    }

    /** Rubric 5: is the person with this cedula a subject of credit? */
    @Transactional(readOnly = true)
    public SujetoCreditoResponse verificarSujetoCredito(String cedula) {
        return clientes.findByCedula(cedula)
                .map(c -> new SujetoCreditoResponse(cedula, c.getCodCliente(), c.getNombre(), evaluarReglas(c)))
                .orElseGet(() -> new SujetoCreditoResponse(cedula, null, null,
                        List.of(new Motivo("NO_ES_CLIENTE", "El solicitante no es cliente del banco"))));
    }

    /** Rubric 6: maximum credit amount for a client code. */
    @Transactional(readOnly = true)
    public MontoMaximoResponse calcularMontoMaximo(Integer codCliente) {
        Cliente cliente = clientes.findById(codCliente).orElseThrow(() -> clienteNoEncontrado(codCliente));
        return montoMaximo(cliente);
    }

    /** Rubric 7: grants the credit when the client qualifies and stores its amortization table. */
    @Transactional
    public CreditoResponse otorgarCredito(OtorgarCreditoRequest request) {
        int cuotas = request.numeroCuotas();
        if (cuotas < props.cuotasMinimas() || cuotas > props.cuotasMaximas()) {
            throw new SolicitudInvalidaException("CUOTAS_FUERA_DE_RANGO", "El plazo debe estar entre %d y %d meses"
                    .formatted(props.cuotasMinimas(), props.cuotasMaximas()));
        }
        Cliente cliente = clientes.findByIdForUpdate(request.codCliente())
                .orElseThrow(() -> clienteNoEncontrado(request.codCliente()));

        List<Motivo> motivos = evaluarReglas(cliente);
        if (!motivos.isEmpty()) {
            throw new CreditoRechazadoException("NO_ES_SUJETO_CREDITO", "El cliente no es sujeto de crédito",
                    motivos, null);
        }
        BigDecimal maximo = montoMaximo(cliente).montoMaximo();
        BigDecimal precio = request.precio().setScale(2, RoundingMode.HALF_UP);
        if (precio.compareTo(maximo) > 0) {
            throw new CreditoRechazadoException("MONTO_EXCEDE_MAXIMO",
                    "El precio %s supera el monto máximo aprobado %s".formatted(precio, maximo), List.of(), maximo);
        }

        PlanPagos plan = calculator.calcular(precio, cuotas, props.tasaInteresAnual());
        Credito credito = new Credito(cliente, precio, cuotas, props.tasaInteresAnual(), plan.valorCuota(), hoy());
        plan.cuotas().forEach(c -> credito.agregarCuota(new TablaAmortizacion(c.numCuota(), c.valorCuota(),
                c.interesPagado(), c.capitalPagado(), c.saldo())));
        return CreditoResponse.from(creditos.save(credito));
    }

    /** Rubric 10: a granted credit with its amortization table. */
    @Transactional(readOnly = true)
    public CreditoResponse obtenerCredito(Integer codCredito) {
        return creditos.findWithTablaByCodCredito(codCredito).map(CreditoResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("CREDITO_NO_ENCONTRADO",
                        "No existe el crédito " + codCredito));
    }

    /** Rules 2-4 (rule 1, being a client, is checked by the caller). Empty list = subject of credit. */
    private List<Motivo> evaluarReglas(Cliente cliente) {
        LocalDate hoy = hoy();
        List<Motivo> motivos = new ArrayList<>();
        if (!movimientos.existeMovimientoDesde(cliente.getCodCliente(), Movimiento.DEPOSITO, hoy.minusMonths(1))) {
            motivos.add(new Motivo("SIN_DEPOSITO_ULTIMO_MES", "El cliente no tiene depósitos en el último mes"));
        }
        if (Cliente.GENERO_MASCULINO.equals(cliente.getGenero())
                && Period.between(cliente.getFechaNacimiento(), hoy).getYears() < EDAD_MINIMA_MASCULINO) {
            motivos.add(new Motivo("MENOR_25_MASCULINO", "El cliente es de género masculino y menor de 25 años"));
        }
        if (creditos.existsByClienteCodClienteAndEstado(cliente.getCodCliente(), Credito.ACTIVO)) {
            motivos.add(new Motivo("CREDITO_ACTIVO", "El cliente ya tiene un crédito activo"));
        }
        return motivos;
    }

    /** ((avg deposits - avg withdrawals of the last 3 months) * 35%) * 6, never below 0. */
    private MontoMaximoResponse montoMaximo(Cliente cliente) {
        LocalDate desde = hoy().minusMonths(3);
        BigDecimal depositos = movimientos.promedio(cliente.getCodCliente(), Movimiento.DEPOSITO, desde)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal retiros = movimientos.promedio(cliente.getCodCliente(), Movimiento.RETIRO, desde)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal maximo = depositos.subtract(retiros)
                .multiply(props.porcentajeMontoMaximo())
                .multiply(BigDecimal.valueOf(props.multiplicadorMontoMaximo()))
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        return new MontoMaximoResponse(cliente.getCodCliente(), depositos, retiros, maximo);
    }

    private LocalDate hoy() {
        return LocalDate.now(clock);
    }

    private static RecursoNoEncontradoException clienteNoEncontrado(Integer codCliente) {
        return new RecursoNoEncontradoException("CLIENTE_NO_ENCONTRADO", "No existe el cliente " + codCliente);
    }
}
