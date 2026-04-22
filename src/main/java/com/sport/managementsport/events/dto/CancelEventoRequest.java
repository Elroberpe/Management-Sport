package com.sport.managementsport.events.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelEventoRequest {

    @NotBlank(message = "El motivo de la cancelación es obligatorio.")
    private String motivo;

    @NotNull(message = "El monto a reembolsar no puede ser nulo.")
    @Min(value = 0, message = "El monto a reembolsar no puede ser negativo.")
    private BigDecimal montoReembolso;

    private String notaReembolso;
}
