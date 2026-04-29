package com.sport.managementsport.dashboard.service;

import com.sport.managementsport.dashboard.dto.ActividadDiariaResponse;
import com.sport.managementsport.dashboard.dto.DisponibilidadDiariaResponse;
import com.sport.managementsport.dashboard.dto.KpiResponse;
import com.sport.managementsport.dashboard.dto.SedeStatusResponse;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    List<DisponibilidadDiariaResponse> getDisponibilidadSemanal(Integer sucursalId, LocalDate fechaBase);
    KpiResponse getTasaOcupacionMensual(Integer sucursalId);
    List<ActividadDiariaResponse> getActividadReservas(Integer sucursalId, String periodo);
    List<SedeStatusResponse> getSedesStatus();
}
