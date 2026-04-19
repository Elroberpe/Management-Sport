package com.sport.managementsport.company.dto;

import com.sport.managementsport.common.enums.EstadoCancha;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCanchaRequest {

    @Size(max = 100, message = "Si se proporciona, el nombre no puede exceder los 100 caracteres")
    private String nombre;

    @DecimalMin(value = "0.01", message = "Si se proporciona, el precio por hora debe ser mayor que cero")
    private BigDecimal precioHora;
}
