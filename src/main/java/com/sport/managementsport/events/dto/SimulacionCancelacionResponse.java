package com.sport.managementsport.events.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class SimulacionCancelacionResponse {
    private BigDecimal montoPagado;
    private BigDecimal penalidadAplicable;
    private BigDecimal reembolsoMaximoPermitido;
    private boolean aplicaPenalidad;
    private String mensaje;
}
