package com.sport.managementsport.finance.dto;

import com.sport.managementsport.common.enums.EstadoPago;
import com.sport.managementsport.common.enums.MetodoPago;
import com.sport.managementsport.common.enums.TipoTransaccion;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class PagoResponse {
    private Integer id;
    private Integer reservaId;
    private Integer eventoId;
    private LocalDate fecha;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private EstadoPago estado;
    private TipoTransaccion tipoTransaccion;
    private String referencia;
    private String nota;
}
