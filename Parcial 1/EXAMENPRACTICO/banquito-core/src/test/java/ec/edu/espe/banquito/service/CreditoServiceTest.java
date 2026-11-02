package ec.edu.espe.banquito.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ec.edu.espe.banquito.config.CreditoProperties;
import ec.edu.espe.banquito.dto.CreditoResponse;
import ec.edu.espe.banquito.dto.MontoMaximoResponse;
import ec.edu.espe.banquito.dto.OtorgarCreditoRequest;
import ec.edu.espe.banquito.dto.SujetoCreditoResponse;
import ec.edu.espe.banquito.exception.CreditoRechazadoException;
import ec.edu.espe.banquito.exception.RecursoNoEncontradoException;
import ec.edu.espe.banquito.exception.SolicitudInvalidaException;
import ec.edu.espe.banquito.model.Cliente;
import ec.edu.espe.banquito.model.Credito;
import ec.edu.espe.banquito.model.Movimiento;
import ec.edu.espe.banquito.repository.ClienteRepository;
import ec.edu.espe.banquito.repository.CreditoRepository;
import ec.edu.espe.banquito.repository.MovimientoRepository;

class CreditoServiceTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 1);
    private static final String CEDULA = "1712345678";

    private ClienteRepository clientes;
    private MovimientoRepository movimientos;
    private CreditoRepository creditos;
    private CreditoService service;

    @BeforeEach
    void setUp() {
        clientes = mock(ClienteRepository.class);
        movimientos = mock(MovimientoRepository.class);
        creditos = mock(CreditoRepository.class);
        Clock clock = Clock.fixed(HOY.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        CreditoProperties props = new CreditoProperties(new BigDecimal("0.35"), 6, new BigDecimal("16.50"), 3, 18);
        service = new CreditoService(clientes, movimientos, creditos, new AmortizacionCalculator(), props, clock);
        when(creditos.save(any(Credito.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---- S5: sujeto de crédito ----

    @Test
    void personWhoIsNotAClientIsNotASubject() {
        when(clientes.findByCedula("1799999999")).thenReturn(Optional.empty());

        SujetoCreditoResponse r = service.verificarSujetoCredito("1799999999");

        assertThat(r.sujetoCredito()).isFalse();
        assertThat(r.codCliente()).isNull();
        assertThat(r.motivos()).extracting("codigo").containsExactly("NO_ES_CLIENTE");
    }

    @Test
    void clientMeetingAllRulesIsASubject() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        habilitar(c);

        SujetoCreditoResponse r = service.verificarSujetoCredito(CEDULA);

        assertThat(r.sujetoCredito()).isTrue();
        assertThat(r.codCliente()).isEqualTo(1);
        assertThat(r.motivos()).isEmpty();
    }

    @Test
    void depositRuleLooksExactlyOneMonthBack() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        habilitar(c);

        service.verificarSujetoCredito(CEDULA);

        verify(movimientos).existeMovimientoDesde(1, Movimiento.DEPOSITO, HOY.minusMonths(1));
    }

    @Test
    void clientWithoutDepositInLastMonthIsNotASubject() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        habilitar(c);
        when(movimientos.existeMovimientoDesde(1, Movimiento.DEPOSITO, HOY.minusMonths(1))).thenReturn(false);

        assertThat(service.verificarSujetoCredito(CEDULA).motivos()).extracting("codigo")
                .containsExactly("SIN_DEPOSITO_ULTIMO_MES");
    }

    @Test
    void maleUnder25IsNotASubject() {
        Cliente c = cliente(1, "M", HOY.minusYears(25).plusDays(1));
        habilitar(c);

        assertThat(service.verificarSujetoCredito(CEDULA).motivos()).extracting("codigo")
                .containsExactly("MENOR_25_MASCULINO");
    }

    @Test
    void maleTurning25TodayIsASubject() {
        Cliente c = cliente(1, "M", HOY.minusYears(25));
        habilitar(c);

        assertThat(service.verificarSujetoCredito(CEDULA).sujetoCredito()).isTrue();
    }

    @Test
    void femaleUnder25IsASubject() {
        Cliente c = cliente(1, "F", HOY.minusYears(22));
        habilitar(c);

        assertThat(service.verificarSujetoCredito(CEDULA).sujetoCredito()).isTrue();
    }

    @Test
    void clientWithActiveCreditIsNotASubject() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        habilitar(c);
        when(creditos.existsByClienteCodClienteAndEstado(1, Credito.ACTIVO)).thenReturn(true);

        assertThat(service.verificarSujetoCredito(CEDULA).motivos()).extracting("codigo")
                .containsExactly("CREDITO_ACTIVO");
    }

    @Test
    void reportsEveryFailedRule() {
        Cliente c = cliente(1, "M", HOY.minusYears(20));
        when(clientes.findByCedula(CEDULA)).thenReturn(Optional.of(c));
        when(creditos.existsByClienteCodClienteAndEstado(1, Credito.ACTIVO)).thenReturn(true);

        assertThat(service.verificarSujetoCredito(CEDULA).motivos()).extracting("codigo")
                .containsExactly("SIN_DEPOSITO_ULTIMO_MES", "MENOR_25_MASCULINO", "CREDITO_ACTIVO");
    }

    // ---- S6: monto máximo ----

    @Test
    void maxAmountIs35PercentOfAverageDifferenceTimesSix() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        when(clientes.findById(1)).thenReturn(Optional.of(c));
        promedios(1, "1212.50", "400.00");

        MontoMaximoResponse r = service.calcularMontoMaximo(1);

        assertThat(r.promedioDepositos()).isEqualByComparingTo("1212.50");
        assertThat(r.promedioRetiros()).isEqualByComparingTo("400.00");
        assertThat(r.montoMaximo()).isEqualByComparingTo("1706.25");
        assertThat(r.montoMaximo().scale()).isEqualTo(2);
    }

    @Test
    void maxAmountNeverGoesBelowZero() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        when(clientes.findById(1)).thenReturn(Optional.of(c));
        promedios(1, "100.00", "300.00");

        assertThat(service.calcularMontoMaximo(1).montoMaximo()).isEqualByComparingTo("0.00");
    }

    @Test
    void maxAmountOfUnknownClientIsNotFound() {
        when(clientes.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.calcularMontoMaximo(99)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    // ---- S7: otorgar crédito ----

    @Test
    void grantsCreditAndBuildsAmortizationTable() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        habilitarParaOtorgar(c, "1212.50", "400.00");

        CreditoResponse r = service.otorgarCredito(new OtorgarCreditoRequest(1, new BigDecimal("450.00"), 6));

        assertThat(r.monto()).isEqualByComparingTo("450.00");
        assertThat(r.numeroCuotas()).isEqualTo(6);
        assertThat(r.tasaAnual()).isEqualByComparingTo("16.50");
        assertThat(r.valorCuota()).isEqualByComparingTo("78.65");
        assertThat(r.estado()).isEqualTo("ACT");
        assertThat(r.fecha()).isEqualTo(HOY);
        assertThat(r.tablaAmortizacion()).hasSize(6);
        assertThat(r.tablaAmortizacion().get(5).saldo()).isEqualByComparingTo("0.00");
        verify(creditos).save(any(Credito.class));
    }

    @Test
    void grantsCreditWhenPriceEqualsMaxAmount() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        habilitarParaOtorgar(c, "1212.50", "400.00");

        CreditoResponse r = service.otorgarCredito(new OtorgarCreditoRequest(1, new BigDecimal("1706.25"), 12));

        assertThat(r.monto()).isEqualByComparingTo("1706.25");
    }

    @Test
    void rejectsPriceAboveMaxAmount() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        habilitarParaOtorgar(c, "1212.50", "400.00");

        assertThatThrownBy(() -> service.otorgarCredito(new OtorgarCreditoRequest(1, new BigDecimal("1706.26"), 12)))
                .isInstanceOfSatisfying(CreditoRechazadoException.class, e -> {
                    assertThat(e.getCodigo()).isEqualTo("MONTO_EXCEDE_MAXIMO");
                    assertThat(e.getMontoMaximo()).isEqualByComparingTo("1706.25");
                });
        verify(creditos, never()).save(any());
    }

    @Test
    void rejectsClientThatIsNotASubject() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        habilitarParaOtorgar(c, "1212.50", "400.00");
        when(creditos.existsByClienteCodClienteAndEstado(1, Credito.ACTIVO)).thenReturn(true);

        assertThatThrownBy(() -> service.otorgarCredito(new OtorgarCreditoRequest(1, new BigDecimal("100.00"), 6)))
                .isInstanceOfSatisfying(CreditoRechazadoException.class, e -> {
                    assertThat(e.getCodigo()).isEqualTo("NO_ES_SUJETO_CREDITO");
                    assertThat(e.getMotivos()).extracting("codigo").containsExactly("CREDITO_ACTIVO");
                });
        verify(creditos, never()).save(any());
    }

    @Test
    void acceptsTermBoundaries() {
        Cliente c = cliente(1, "F", HOY.minusYears(30));
        habilitarParaOtorgar(c, "1212.50", "400.00");

        assertThat(service.otorgarCredito(new OtorgarCreditoRequest(1, new BigDecimal("300.00"), 3)).tablaAmortizacion()).hasSize(3);
        assertThat(service.otorgarCredito(new OtorgarCreditoRequest(1, new BigDecimal("300.00"), 18)).tablaAmortizacion()).hasSize(18);
    }

    @Test
    void rejectsTermOutsideThreeToEighteenMonths() {
        for (int cuotas : new int[] {2, 19}) {
            assertThatThrownBy(() -> service.otorgarCredito(new OtorgarCreditoRequest(1, new BigDecimal("300.00"), cuotas)))
                    .isInstanceOfSatisfying(SolicitudInvalidaException.class,
                            e -> assertThat(e.getCodigo()).isEqualTo("CUOTAS_FUERA_DE_RANGO"));
        }
        verify(creditos, never()).save(any());
    }

    @Test
    void grantToUnknownClientIsNotFound() {
        when(clientes.findByIdForUpdate(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.otorgarCredito(new OtorgarCreditoRequest(99, new BigDecimal("300.00"), 6)))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    // ---- S9: tabla de amortización ----

    @Test
    void amortizationOfUnknownCreditIsNotFound() {
        when(creditos.findWithTablaByCodCredito(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerCredito(99)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    // ---- helpers ----

    private static Cliente cliente(int cod, String genero, LocalDate nacimiento) {
        return new Cliente(cod, CEDULA, "Cliente " + cod, genero, nacimiento);
    }

    private void habilitar(Cliente c) {
        when(clientes.findByCedula(CEDULA)).thenReturn(Optional.of(c));
        when(clientes.findByIdForUpdate(c.getCodCliente())).thenReturn(Optional.of(c));
        when(clientes.findById(c.getCodCliente())).thenReturn(Optional.of(c));
        when(movimientos.existeMovimientoDesde(c.getCodCliente(), Movimiento.DEPOSITO, HOY.minusMonths(1))).thenReturn(true);
        when(creditos.existsByClienteCodClienteAndEstado(c.getCodCliente(), Credito.ACTIVO)).thenReturn(false);
    }

    private void habilitarParaOtorgar(Cliente c, String promDep, String promRet) {
        habilitar(c);
        promedios(c.getCodCliente(), promDep, promRet);
    }

    private void promedios(int cod, String dep, String ret) {
        when(movimientos.promedio(cod, Movimiento.DEPOSITO, HOY.minusMonths(3))).thenReturn(new BigDecimal(dep));
        when(movimientos.promedio(cod, Movimiento.RETIRO, HOY.minusMonths(3))).thenReturn(new BigDecimal(ret));
    }
}
