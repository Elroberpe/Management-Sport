package com.sport.managementsport.events.service;

import com.sport.managementsport.events.domain.Mantenimiento;
import java.util.List;
import java.util.Optional;

public interface MantenimientoService {

    Mantenimiento createMantenimiento(Mantenimiento mantenimiento);

    Optional<Mantenimiento> getMantenimientoById(Integer id);

    List<Mantenimiento> getAllMantenimientos();

    Mantenimiento updateMantenimiento(Integer id, Mantenimiento mantenimientoDetails);

    void deleteMantenimiento(Integer id);
}
