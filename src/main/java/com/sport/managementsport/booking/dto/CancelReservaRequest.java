package com.sport.managementsport.booking.dto;

import com.sport.managementsport.common.enums.MetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelReservaRequest {

    @NotBlank(message = "El motivo de la cancelación es obligatorio")
    @Size(max = 250, message = "El motivo no puede exceder los 250 caracteres")
    private String motivo;

    private MetodoPago metodoPagoReembolso;
}
