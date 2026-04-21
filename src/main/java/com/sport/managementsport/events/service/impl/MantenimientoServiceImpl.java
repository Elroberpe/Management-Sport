package com.sport.managementsport.events.service.impl;

import com.sport.managementsport.booking.service.ReservaService;
import com.sport.managementsport.common.enums.EstadoMantenimiento;
import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.company.service.CanchaService;
import com.sport.managementsport.events.domain.Mantenimiento;
import com.sport.managementsport.events.dto.CreateMantenimientoRequest;
import com.sport.managementsport.events.dto.MantenimientoResponse;
import com.sport.managementsport.events.dto.UpdateEstadoMantenimientoRequest;
import com.sport.managementsport.events.dto.UpdateMantenimientoRequest;
import com.sport.managementsport.events.repository.MantenimientoRepository;
import com.sport.managementsport.events.repository.MantenimientoSpecification;
import com.sport.managementsport.events.service.MantenimientoService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MantenimientoServiceImpl implements MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final CanchaService canchaService;
    private final ReservaService reservaService;

    public MantenimientoServiceImpl(MantenimientoRepository mantenimientoRepository, CanchaService canchaService, @Lazy ReservaService reservaService) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.canchaService = canchaService;
        this.reservaService = reservaService;
    }

    @Override
    @Transactional
    public MantenimientoResponse createMantenimiento(CreateMantenimientoRequest request) {
        Cancha cancha = canchaService.findCanchaEntityById(request.getCanchaId());

        List<Mantenimiento> conflictingMantenimientos = mantenimientoRepository.findConflictingMantenimientos(
                request.getCanchaId(), request.getHoraInicio(), request.getHoraFin());
        if (!conflictingMantenimientos.isEmpty()) {
            throw new BusinessRuleException("El horario seleccionado se solapa con otro mantenimiento.");
        }

        if (!reservaService.findConflictingReservas(request.getCanchaId(), request.getHoraInicio(), request.getHoraFin()).isEmpty()) {
            throw new BusinessRuleException("El horario seleccionado se solapa con una reserva existente.");
        }

        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setCancha(cancha);
        mantenimiento.setHoraInicio(request.getHoraInicio());
        mantenimiento.setHoraFin(request.getHoraFin());
        mantenimiento.setTipoMantenimiento(request.getTipoMantenimiento());
        mantenimiento.setMotivo(request.getMotivo());

        Mantenimiento savedMantenimiento = mantenimientoRepository.save(mantenimiento);
        return toMantenimientoResponse(savedMantenimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public MantenimientoResponse getMantenimientoById(Integer id) {
        return mantenimientoRepository.findById(id)
                .map(this::toMantenimientoResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Mantenimiento no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MantenimientoResponse> getAllMantenimientos(Integer sucursalId, Pageable pageable) {
        Specification<Mantenimiento> spec = Specification.where(MantenimientoSpecification.sucursalIdEquals(sucursalId));
        return mantenimientoRepository.findAll(spec, pageable).map(this::toMantenimientoResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoResponse> getMantenimientosByCanchaId(Integer canchaId) {
        if (!canchaService.canchaExists(canchaId)) {
            throw new ResourceNotFoundException("Cancha no encontrada con id: " + canchaId);
        }
        return mantenimientoRepository.findByCanchaCanchaId(canchaId).stream()
                .map(this::toMantenimientoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MantenimientoResponse updateMantenimiento(Integer id, UpdateMantenimientoRequest request) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mantenimiento no encontrado con id: " + id));

        if (mantenimiento.getEstadoMantenimiento() == EstadoMantenimiento.COMPLETADO || mantenimiento.getEstadoMantenimiento() == EstadoMantenimiento.CANCELADO) {
            throw new BusinessRuleException("No se puede modificar un mantenimiento completado o cancelado.");
        }

        if (request.getHoraInicio() != null) mantenimiento.setHoraInicio(request.getHoraInicio());
        if (request.getHoraFin() != null) mantenimiento.setHoraFin(request.getHoraFin());
        if (request.getTipoMantenimiento() != null) mantenimiento.setTipoMantenimiento(request.getTipoMantenimiento());
        if (request.getMotivo() != null) mantenimiento.setMotivo(request.getMotivo());

        Mantenimiento updated = mantenimientoRepository.save(mantenimiento);
        return toMantenimientoResponse(updated);
    }

    @Override
    @Transactional
    public MantenimientoResponse updateEstado(Integer id, UpdateEstadoMantenimientoRequest request) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mantenimiento no encontrado con id: " + id));
        
        mantenimiento.setEstadoMantenimiento(request.getEstado());
        Mantenimiento updated = mantenimientoRepository.save(mantenimiento);
        return toMantenimientoResponse(updated);
    }

    @Override
    @Transactional
    public MantenimientoResponse cancelarMantenimiento(Integer id) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mantenimiento no encontrado con id: " + id));

        if (mantenimiento.getEstadoMantenimiento() == EstadoMantenimiento.COMPLETADO || mantenimiento.getEstadoMantenimiento() == EstadoMantenimiento.CANCELADO) {
            throw new BusinessRuleException("El mantenimiento ya está en un estado final.");
        }

        mantenimiento.setEstadoMantenimiento(EstadoMantenimiento.CANCELADO);
        Mantenimiento updated = mantenimientoRepository.save(mantenimiento);
        return toMantenimientoResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mantenimiento> findConflictingMantenimientos(Integer canchaId, LocalDateTime horaInicio, LocalDateTime horaFin) {
        return mantenimientoRepository.findConflictingMantenimientos(canchaId, horaInicio, horaFin);
    }

    private MantenimientoResponse toMantenimientoResponse(Mantenimiento mantenimiento) {
        return MantenimientoResponse.builder()
                .id(mantenimiento.getMantenimientoId())
                .canchaId(mantenimiento.getCancha().getCanchaId())
                .nombreCancha(mantenimiento.getCancha().getNombre())
                .horaInicio(mantenimiento.getHoraInicio())
                .horaFin(mantenimiento.getHoraFin())
                .tipoMantenimiento(mantenimiento.getTipoMantenimiento())
                .estadoMantenimiento(mantenimiento.getEstadoMantenimiento())
                .motivo(mantenimiento.getMotivo())
                .build();
    }
}
