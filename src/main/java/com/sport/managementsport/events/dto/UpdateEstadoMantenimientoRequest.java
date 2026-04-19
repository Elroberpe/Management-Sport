package com.sport.managementsport.events.dto;

import com.sport.managementsport.common.enums.EstadoMantenimiento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEstadoMantenimientoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoMantenimiento estado;
}
