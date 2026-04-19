package com.sport.managementsport.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnularPagoRequest {

    @NotBlank(message = "El motivo de la anulación es obligatorio")
    @Size(max = 250)
    private String motivo;
}
