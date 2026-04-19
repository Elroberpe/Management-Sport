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
@ValidTimeRange // Aplicamos la validación personalizada a nivel de clase
public class CreateReservaRequest {

    @NotNull(message = "El ID de la cancha es obligatorio")
    private Integer canchaId;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Integer clienteId;

    @NotNull(message = "La fecha de la reserva es obligatoria")
    @FutureOrPresent(message = "La fecha de la reserva no puede ser en el pasado")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;
}
