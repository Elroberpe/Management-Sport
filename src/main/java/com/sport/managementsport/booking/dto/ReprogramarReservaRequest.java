package com.sport.managementsport.booking.dto;

import com.sport.managementsport.booking.validation.ValidTimeRange;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidTimeRange(groups = ReprogramarReservaRequest.class) // Usamos un grupo de validación
public class ReprogramarReservaRequest {

    @NotNull(message = "La nueva fecha es obligatoria")
    @FutureOrPresent(message = "La nueva fecha no puede ser en el pasado")
    private LocalDate nuevaFecha;

    @NotNull(message = "La nueva hora de inicio es obligatoria")
    private LocalTime nuevaHoraInicio;

    @NotNull(message = "La nueva hora de fin es obligatoria")
    private LocalTime nuevaHoraFin;
}
