package com.sport.managementsport.company.service;

import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.dto.CanchaResponse;
import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.dto.UpdateSucursalRequest;

import java.util.List;

public interface SucursalService {

    SucursalResponse createSucursal(CreateSucursalRequest request);
    SucursalResponse getSucursalById(Integer id); // <-- Firma corregida
    List<SucursalResponse> getAllSucursales(Integer empresaId); // <-- Modificado
    List<CanchaResponse> getCanchasBySucursalId(Integer sucursalId);
    SucursalResponse updateSucursal(Integer id, UpdateSucursalRequest request);
    void deleteSucursal(Integer id);
    SucursalResponse activarSucursal(Integer id);
    SucursalResponse desactivarSucursal(Integer id);

    Sucursal findSucursalEntityById(Integer id);
    boolean hasSucursales(Integer empresaId);
    boolean sucursalExists(Integer id);
}