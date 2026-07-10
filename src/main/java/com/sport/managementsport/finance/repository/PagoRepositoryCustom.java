package com.sport.managementsport.finance.repository;

import com.sport.managementsport.common.enums.MetodoPago;
import com.sport.managementsport.finance.dto.PagoStatsResponse;

import java.time.LocalDate;

public interface PagoRepositoryCustom {
    PagoStatsResponse getPagoStats(LocalDate desde, LocalDate hasta, MetodoPago metodo, Integer sucursalId);
}
