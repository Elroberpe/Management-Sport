package com.sport.managementsport.company.service;

import com.sport.managementsport.common.enums.EstadoCancha;
import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.company.dto.CanchaResponse;
import com.sport.managementsport.company.dto.CreateCanchaRequest;
import com.sport.managementsport.company.dto.UpdateCanchaRequest;
import com.sport.managementsport.company.dto.UpdateEstadoCanchaRequest;

import java.util.List;
import java.util.Optional;

public interface CanchaService {

    CanchaResponse createCancha(CreateCanchaRequest request);
    Optional<CanchaResponse> getCanchaById(Integer id);
    List<CanchaResponse> getAllCanchas(Integer sucursalId, EstadoCancha estado);
    CanchaResponse updateCancha(Integer id, UpdateCanchaRequest request);
    CanchaResponse updateEstadoCancha(Integer id, UpdateEstadoCanchaRequest request);
    void deleteCancha(Integer id);
    List<CanchaResponse> getCanchasBySucursalId(Integer sucursalId);
    Cancha findCanchaEntityById(Integer id);
    boolean canchaExists(Integer id);

    // Nuevo método para validación
    boolean hasCanchasInSucursal(Integer sucursalId);
}
