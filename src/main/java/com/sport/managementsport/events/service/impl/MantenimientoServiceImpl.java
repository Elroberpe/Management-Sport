package com.sport.managementsport.events.service.impl;

import com.sport.managementsport.events.domain.Mantenimiento;
import com.sport.managementsport.events.repository.MantenimientoRepository;
import com.sport.managementsport.events.service.MantenimientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MantenimientoServiceImpl implements MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;

    public MantenimientoServiceImpl(MantenimientoRepository mantenimientoRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
    }

    @Override
    @Transactional
    public Mantenimiento createMantenimiento(Mantenimiento mantenimiento) {
        // Aquí se podrían añadir validaciones, como verificar que la cancha no esté ocupada
        return mantenimientoRepository.save(mantenimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Mantenimiento> getMantenimientoById(Integer id) {
        return mantenimientoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mantenimiento> getAllMantenimientos() {
        return mantenimientoRepository.findAll();
    }

    @Override
    @Transactional
    public Mantenimiento updateMantenimiento(Integer id, Mantenimiento mantenimientoDetails) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado con id: " + id));

        mantenimiento.setHoraInicio(mantenimientoDetails.getHoraInicio());
        mantenimiento.setHoraFin(mantenimientoDetails.getHoraFin());
        mantenimiento.setTipoMantenimiento(mantenimientoDetails.getTipoMantenimiento());
        mantenimiento.setEstadoMantenimiento(mantenimientoDetails.getEstadoMantenimiento());
        mantenimiento.setMotivo(mantenimientoDetails.getMotivo());

        return mantenimientoRepository.save(mantenimiento);
    }

    @Override
    @Transactional
    public void deleteMantenimiento(Integer id) {
        if (!mantenimientoRepository.existsById(id)) {
            throw new RuntimeException("Mantenimiento no encontrado con id: " + id);
        }
        mantenimientoRepository.deleteById(id);
    }
}
