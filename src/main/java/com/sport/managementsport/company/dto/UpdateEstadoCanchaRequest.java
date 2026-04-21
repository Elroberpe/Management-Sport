package com.sport.managementsport.company.dto;

import com.sport.managementsport.common.enums.EstadoCancha;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEstadoCanchaRequest {

    @NotNull(message = "El nuevo estado de la cancha es obligatorio")
    private EstadoCancha estadoCancha;
}
