package com.sport.managementsport.events.service;

import com.sport.managementsport.events.domain.Mantenimiento;
import com.sport.managementsport.events.dto.CreateMantenimientoRequest;
import com.sport.managementsport.events.dto.MantenimientoResponse;
import com.sport.managementsport.events.dto.UpdateEstadoMantenimientoRequest;
import com.sport.managementsport.events.dto.UpdateMantenimientoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MantenimientoService {
    
    MantenimientoResponse createMantenimiento(CreateMantenimientoRequest request);
    MantenimientoResponse getMantenimientoById(Integer id);
    Page<MantenimientoResponse> getAllMantenimientos(Pageable pageable);
    List<MantenimientoResponse> getMantenimientosByCanchaId(Integer canchaId);
    MantenimientoResponse updateMantenimiento(Integer id, UpdateMantenimientoRequest request);
    MantenimientoResponse updateEstado(Integer id, UpdateEstadoMantenimientoRequest request);
    MantenimientoResponse cancelarMantenimiento(Integer id);

    // Método para uso interno
    List<Mantenimiento> findConflictingMantenimientos(Integer canchaId, LocalDateTime horaInicio, LocalDateTime horaFin);
}
