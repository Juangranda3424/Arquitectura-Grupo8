package ec.edu.espe.banquito.dto;

import java.util.List;

public record SujetoCreditoResponse(String cedula, Integer codCliente, String nombre, boolean sujetoCredito,
        List<Motivo> motivos) {

    public SujetoCreditoResponse(String cedula, Integer codCliente, String nombre, List<Motivo> motivos) {
        this(cedula, codCliente, nombre, motivos.isEmpty(), List.copyOf(motivos));
    }
}
