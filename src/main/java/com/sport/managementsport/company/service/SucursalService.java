package com.sport.managementsport.company.service;

import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.dto.UpdateSucursalRequest;

import java.util.List;

public interface SucursalService {

    SucursalResponse createSucursal(CreateSucursalRequest request);
    SucursalResponse getSucursalById(Integer id); // <-- Firma corregida
    List<SucursalResponse> getAllSucursales();
    SucursalResponse updateSucursal(Integer id, UpdateSucursalRequest request);
    void deleteSucursal(Integer id);
    List<SucursalResponse> getSucursalesByEmpresaId(Integer empresaId);
    SucursalResponse activarSucursal(Integer id);
    SucursalResponse desactivarSucursal(Integer id);
    Sucursal findSucursalEntityById(Integer id);
    boolean hasSucursales(Integer empresaId);
    boolean sucursalExists(Integer id);
}
