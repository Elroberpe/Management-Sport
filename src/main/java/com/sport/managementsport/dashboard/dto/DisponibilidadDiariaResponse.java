package com.sport.managementsport.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadDiariaResponse {
    private String dia; // "LUNES", "MARTES", etc.
    private LocalDate fecha;
    private BigDecimal horasDisponibles;
}
