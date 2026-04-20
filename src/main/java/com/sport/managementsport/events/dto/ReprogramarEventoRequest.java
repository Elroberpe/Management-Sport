package com.sport.managementsport.events.dto;

import com.sport.managementsport.events.dto.CreateEventoRequest.HorarioBloqueoDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReprogramarEventoRequest {

    @NotEmpty(message = "Se debe especificar al menos un nuevo horario para el evento")
    private List<@Valid HorarioBloqueoDto> nuevosHorarios;
}
