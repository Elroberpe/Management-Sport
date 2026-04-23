package com.sport.managementsport.dashboard.service;

import com.sport.managementsport.dashboard.dto.DisponibilidadDiariaResponse;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    List<DisponibilidadDiariaResponse> getDisponibilidadSemanal(Integer sucursalId, LocalDate fechaBase);
}
