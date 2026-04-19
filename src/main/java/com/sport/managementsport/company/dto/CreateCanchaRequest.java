package com.sport.managementsport.company.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateCanchaRequest {

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Integer sucursalId;

    @NotBlank(message = "El nombre de la cancha es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotNull(message = "El precio por hora es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio por hora debe ser mayor que cero")
    private BigDecimal precioHora;
}
