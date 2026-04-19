package com.sport.managementsport.booking.dto;

import com.sport.managementsport.common.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddPagoToReservaRequest {

    @NotNull(message = "El monto del pago es obligatorio")
    @Positive(message = "El monto debe ser un valor positivo")
    private BigDecimal monto;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;
}
