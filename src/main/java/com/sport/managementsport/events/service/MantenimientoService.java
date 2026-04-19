package com.sport.managementsport.events.service;

import com.sport.managementsport.events.domain.Mantenimiento;
import java.time.LocalDateTime;
import java.util.List;

public interface MantenimientoService {
    // Este método es para uso interno entre servicios
    List<Mantenimiento> findConflictingMantenimientos(Integer canchaId, LocalDateTime horaInicio, LocalDateTime horaFin);
}
