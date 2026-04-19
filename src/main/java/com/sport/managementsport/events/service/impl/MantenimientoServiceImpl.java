package com.sport.managementsport.events.service.impl;

import com.sport.managementsport.events.domain.Mantenimiento;
import com.sport.managementsport.events.repository.MantenimientoRepository;
import com.sport.managementsport.events.service.MantenimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MantenimientoServiceImpl implements MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Mantenimiento> findConflictingMantenimientos(Integer canchaId, LocalDateTime horaInicio, LocalDateTime horaFin) {
        return mantenimientoRepository.findConflictingMantenimientos(canchaId, horaInicio, horaFin);
    }
}
