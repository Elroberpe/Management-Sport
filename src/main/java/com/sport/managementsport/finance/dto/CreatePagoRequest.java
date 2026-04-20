package com.sport.managementsport.finance.dto;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.common.enums.MetodoPago;
import com.sport.managementsport.common.enums.TipoTransaccion;
import com.sport.managementsport.events.domain.Evento;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreatePagoRequest {
    private Reserva reserva;
    private Evento evento; // Campo que faltaba
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private TipoTransaccion tipoTransaccion;
    private String nota;
}
