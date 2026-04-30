package com.sport.managementsport.dashboard.controller;

import com.sport.managementsport.dashboard.dto.ActividadDiariaResponse;
import com.sport.managementsport.dashboard.dto.DisponibilidadDiariaResponse;
import com.sport.managementsport.dashboard.dto.KpiResponse;
import com.sport.managementsport.dashboard.dto.SedeStatusResponse;
import com.sport.managementsport.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/sucursales/{sucursalId}/disponibilidad-semanal")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<DisponibilidadDiariaResponse>> getDisponibilidadSemanal(
            @PathVariable Integer sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaBase) {
        
        LocalDate fecha = (fechaBase == null) ? LocalDate.now() : fechaBase;
        List<DisponibilidadDiariaResponse> disponibilidad = dashboardService.getDisponibilidadSemanal(sucursalId, fecha);
        return ResponseEntity.ok(disponibilidad);
    }

    @GetMapping("/kpi/tasa-ocupacion-mensual")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<KpiResponse> getTasaOcupacionMensual(
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(dashboardService.getTasaOcupacionMensual(sucursalId));
    }

    @GetMapping("/graficos/actividad-reservas")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<ActividadDiariaResponse>> getActividadReservas(
            @RequestParam(required = false) Integer sucursalId,
            @RequestParam(defaultValue = "MES") String periodo) {
        return ResponseEntity.ok(dashboardService.getActividadReservas(sucursalId, periodo));
    }

    @GetMapping("/status-sedes")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<SedeStatusResponse>> getSedesStatus() {
        return ResponseEntity.ok(dashboardService.getSedesStatus());
    }
}
