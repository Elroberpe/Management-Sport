package com.sport.managementsport.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservaRequest {

    // Permite reasignar la reserva a otro cliente
    private Integer clienteId;

    // Otros campos como notas o metadatos podrían ir aquí en el futuro
}
