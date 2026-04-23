package com.sport.managementsport.dashboard.controller;

import com.sport.managementsport.dashboard.dto.DisponibilidadDiariaResponse;
import com.sport.managementsport.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/sucursales/{sucursalId}/disponibilidad-semanal")
    public ResponseEntity<List<DisponibilidadDiariaResponse>> getDisponibilidadSemanal(
            @PathVariable Integer sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaBase) {
        
        LocalDate fecha = (fechaBase == null) ? LocalDate.now() : fechaBase;
        List<DisponibilidadDiariaResponse> disponibilidad = dashboardService.getDisponibilidadSemanal(sucursalId, fecha);
        return ResponseEntity.ok(disponibilidad);
    }
}
