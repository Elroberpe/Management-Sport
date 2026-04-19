package com.sport.managementsport.company.service;

import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.dto.UpdateSucursalRequest;

import java.util.List;
import java.util.Optional;

public interface SucursalService {
    // ... métodos existentes
    SucursalResponse updateSucursal(Integer id, UpdateSucursalRequest request);
    void deleteSucursal(Integer id);
    List<SucursalResponse> getSucursalesByEmpresaId(Integer empresaId);
    SucursalResponse activarSucursal(Integer id);
    SucursalResponse desactivarSucursal(Integer id);

    // Método para uso interno
    Sucursal findSucursalEntityById(Integer id);
}
