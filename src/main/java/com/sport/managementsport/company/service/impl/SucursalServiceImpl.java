package com.sport.managementsport.company.service.impl;

import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.repository.SucursalRepository;
import com.sport.managementsport.company.service.SucursalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;

    public SucursalServiceImpl(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    @Transactional
    public Sucursal createSucursal(Sucursal sucursal) {
        // Aquí se podrían añadir validaciones, como verificar que la empresa exista
        return sucursalRepository.save(sucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sucursal> getSucursalById(Integer id) {
        return sucursalRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sucursal> getAllSucursales() {
        return sucursalRepository.findAll();
    }

    @Override
    @Transactional
    public Sucursal updateSucursal(Integer id, Sucursal sucursalDetails) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con id: " + id));

        sucursal.setNombre(sucursalDetails.getNombre());
        sucursal.setDireccion(sucursalDetails.getDireccion());
        sucursal.setTelefono(sucursalDetails.getTelefono());
        sucursal.setActivo(sucursalDetails.isActivo());
        // La empresa (empresa_id) no debería cambiarse en una actualización simple

        return sucursalRepository.save(sucursal);
    }

    @Override
    @Transactional
    public void deleteSucursal(Integer id) {
        if (!sucursalRepository.existsById(id)) {
            throw new RuntimeException("Sucursal no encontrada con id: " + id);
        }
        sucursalRepository.deleteById(id);
    }
}
