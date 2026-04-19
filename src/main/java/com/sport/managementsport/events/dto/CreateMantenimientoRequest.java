package com.sport.managementsport.events.dto;

import com.sport.managementsport.common.enums.TipoMantenimiento;
import com.sport.managementsport.events.validation.ValidDateTimeRange;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidDateTimeRange
public class CreateMantenimientoRequest {

    @NotNull(message = "El ID de la cancha es obligatorio")
    private Integer canchaId;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Future(message = "La hora de inicio debe ser en el futuro")
    private LocalDateTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    @Future(message = "La hora de fin debe ser en el futuro")
    private LocalDateTime horaFin;

    @NotNull(message = "El tipo de mantenimiento es obligatorio")
    private TipoMantenimiento tipoMantenimiento;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 200, message = "El motivo no puede exceder los 200 caracteres")
    private String motivo;
}
