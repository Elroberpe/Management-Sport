package com.sport.managementsport.company.service;

import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;

import java.util.List;
import java.util.Optional;

public interface SucursalService {

    SucursalResponse createSucursal(CreateSucursalRequest request);

    Optional<SucursalResponse> getSucursalById(Integer id);

    List<SucursalResponse> getAllSucursales();

    // Nota: No hemos creado un UpdateSucursalRequest, lo haremos si es necesario.
    // SucursalResponse updateSucursal(Integer id, UpdateSucursalRequest request);

    void deleteSucursal(Integer id);

    List<SucursalResponse> getSucursalesByEmpresaId(Integer empresaId);
}
