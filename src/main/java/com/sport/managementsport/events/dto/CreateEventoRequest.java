package com.sport.managementsport.events.dto;

import com.sport.managementsport.common.enums.TipoEvento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventoRequest {

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Integer sucursalId;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Integer clienteId;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 150)
    private String nombre;

    @Size(max = 4000)
    private String descripcion;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipoEvento;

    @Positive(message = "El monto pactado debe ser un valor positivo")
    private BigDecimal montoPactado;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @FutureOrPresent
    private LocalDate fechaFin;

    @NotEmpty(message = "Se debe especificar al menos un horario para el evento")
    private List<@Valid HorarioBloqueoDto> horarios;

    // DTO anidado para los horarios
    @Getter
    @Setter
    public static class HorarioBloqueoDto {
        @NotNull
        private Integer canchaId;
        @NotNull
        private LocalDate fecha;
        @NotNull
        private LocalTime horaInicio;
        @NotNull
        private LocalTime horaFin;
    }
}
