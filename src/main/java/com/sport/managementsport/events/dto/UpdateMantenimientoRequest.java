package com.sport.managementsport.events.dto;

import com.sport.managementsport.common.enums.TipoMantenimiento;
import com.sport.managementsport.events.validation.ValidDateTimeRange;
import jakarta.validation.constraints.Future;
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
public class UpdateMantenimientoRequest {

    @Future(message = "La hora de inicio debe ser en el futuro")
    private LocalDateTime horaInicio;

    @Future(message = "La hora de fin debe ser en el futuro")
    private LocalDateTime horaFin;

    private TipoMantenimiento tipoMantenimiento;

    @Size(max = 200, message = "El motivo no puede exceder los 200 caracteres")
    private String motivo;
}
