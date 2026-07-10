package com.sport.managementsport.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoStatsResponse {
    private BigDecimal totalIngresos;
    private BigDecimal totalSalidas;
    private BigDecimal totalMontoAnulado;
    private Long totalPagosIngreso;
    private Long totalPagosSalida;
    private Long totalPagosAnulados;
}
