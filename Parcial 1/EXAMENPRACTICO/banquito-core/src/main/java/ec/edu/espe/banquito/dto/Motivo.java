package ec.edu.espe.banquito.dto;

/** A failed credit rule: machine code plus a readable message. */
public record Motivo(String codigo, String mensaje) {
}
