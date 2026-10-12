package ec.edu.monster.dto;

public class ConversionDTO {
    // Usar la clase envoltorio Double en lugar del primitivo double
    private Double valorOrigen;
    private Double valorResultado;
    private String unidadOrigen;
    private String unidadDestino;
    private String tipoMagnitud;

    public ConversionDTO() {}

    public ConversionDTO(Double valorOrigen, Double valorResultado, String unidadOrigen, String unidadDestino, String tipoMagnitud) {
        this.valorOrigen = valorOrigen;
        this.valorResultado = valorResultado;
        this.unidadOrigen = unidadOrigen;
        this.unidadDestino = unidadDestino;
        this.tipoMagnitud = tipoMagnitud;
    }

    public Double getValorOrigen() { return valorOrigen; }
    public void setValorOrigen(Double valorOrigen) { this.valorOrigen = valorOrigen; }

    public Double getValorResultado() { return valorResultado; }
    public void setValorResultado(Double valorResultado) { this.valorResultado = valorResultado; }

    public String getUnidadOrigen() { return unidadOrigen; }
    public void setUnidadOrigen(String unidadOrigen) { this.unidadOrigen = unidadOrigen; }

    public String getUnidadDestino() { return unidadDestino; }
    public void setUnidadDestino(String unidadDestino) { this.unidadDestino = unidadDestino; }

    public String getTipoMagnitud() { return tipoMagnitud; }
    public void setTipoMagnitud(String tipoMagnitud) { this.tipoMagnitud = tipoMagnitud; }
}