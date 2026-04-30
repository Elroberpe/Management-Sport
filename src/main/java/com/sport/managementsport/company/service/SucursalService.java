package com.sport.managementsport.company.service;

import com.sport.managementsport.company.domain.Sucursal;
import java.util.List;
import java.util.Optional;

public interface SucursalService {

    Sucursal createSucursal(Sucursal sucursal);

    Optional<Sucursal> getSucursalById(Integer id);

    List<Sucursal> getAllSucursales();

    Sucursal updateSucursal(Integer id, Sucursal sucursalDetails);

    void deleteSucursal(Integer id);
}
