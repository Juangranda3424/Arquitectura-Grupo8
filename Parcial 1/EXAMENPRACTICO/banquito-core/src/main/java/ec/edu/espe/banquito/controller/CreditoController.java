package ec.edu.espe.banquito.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.espe.banquito.dto.CreditoResponse;
import ec.edu.espe.banquito.dto.MontoMaximoResponse;
import ec.edu.espe.banquito.dto.OtorgarCreditoRequest;
import ec.edu.espe.banquito.dto.SujetoCreditoResponse;
import ec.edu.espe.banquito.service.CreditoService;
import jakarta.validation.Valid;

/** BanQuito credit module REST services consumed by the phone retailer. */
@RestController
@RequestMapping("/api/creditos")
@Validated
public class CreditoController {

    private final CreditoService service;

    public CreditoController(CreditoService service) {
        this.service = service;
    }

    /** Rubric 5: GET /api/creditos/sujeto-credito?cedula=1712345678 */
    @GetMapping("/sujeto-credito")
    public SujetoCreditoResponse sujetoCredito(@RequestParam String cedula) {
        return service.verificarSujetoCredito(cedula.trim());
    }

    /** Rubric 6: GET /api/creditos/monto-maximo/1 */
    @GetMapping("/monto-maximo/{codCliente}")
    public MontoMaximoResponse montoMaximo(@PathVariable Integer codCliente) {
        return service.calcularMontoMaximo(codCliente);
    }

    /** Rubric 7: POST /api/creditos {"codCliente":1,"precio":450.00,"numeroCuotas":6} */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditoResponse otorgar(@Valid @RequestBody OtorgarCreditoRequest request) {
        return service.otorgarCredito(request);
    }

    /** Rubric 10: GET /api/creditos/1/amortizacion */
    @GetMapping("/{codCredito}/amortizacion")
    public CreditoResponse amortizacion(@PathVariable Integer codCredito) {
        return service.obtenerCredito(codCredito);
    }
}
