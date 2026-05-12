package com.sport.managementsport.company.service.impl;

import com.sport.managementsport.company.domain.Empresa;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.dto.CanchaResponse;
import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.dto.UpdateSucursalRequest;
import com.sport.managementsport.company.repository.SucursalRepository;
import com.sport.managementsport.company.service.CanchaService;
import com.sport.managementsport.company.service.EmpresaService;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.exception.DuplicateResourceException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import com.sport.managementsport.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final EmpresaService empresaService;
    private final CanchaService canchaService;

    @Override
    @Transactional
    public SucursalResponse createSucursal(CreateSucursalRequest request) {
        Empresa empresa = empresaService.findEmpresaEntityById(request.getEmpresaId());

        if (sucursalRepository.existsByNombreAndEmpresaEmpresaId(request.getNombre(), request.getEmpresaId())) {
            throw new DuplicateResourceException("Ya existe una sucursal con el nombre '" + request.getNombre() + "' para esta empresa.");
        }

        Sucursal sucursal = new Sucursal();
        sucursal.setEmpresa(empresa);
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setActivo(true); // Activo por defecto

        Sucursal savedSucursal = sucursalRepository.save(sucursal);
        return toSucursalResponse(savedSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponse getSucursalById(Integer id) {
        return toSucursalResponse(findSucursalEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalResponse> getAllSucursales(Integer empresaId) {
        if (empresaId != null) {
            if (!empresaService.empresaExists(empresaId)) {
                throw new ResourceNotFoundException("Empresa no encontrada con id: " + empresaId);
            }
        }
        return sucursalRepository.findByEmpresaEmpresaId(empresaId).stream()
                    .map(this::toSucursalResponse)
                    .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public List<CanchaResponse> getCanchasBySucursalId(Integer sucursalId) {
        if (!sucursalExists(sucursalId)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con id: " + sucursalId);
        }
        return canchaService.getCanchasBySucursalId(sucursalId);
    }

    @Override
    @Transactional
    public SucursalResponse updateSucursal(Integer id, UpdateSucursalRequest request) {
        Sucursal sucursal = findSucursalEntityById(id);

        if (request.getNombre() != null && !request.getNombre().equals(sucursal.getNombre())) {
            if (sucursalRepository.existsByNombreAndEmpresaEmpresaId(request.getNombre(), sucursal.getEmpresa().getEmpresaId())) {
                throw new DuplicateResourceException("Ya existe otra sucursal con el nombre '" + request.getNombre() + "' en esta empresa.");
            }
            sucursal.setNombre(request.getNombre());
        }

        if (request.getDireccion() != null) sucursal.setDireccion(request.getDireccion());
        if (request.getTelefono() != null) sucursal.setTelefono(request.getTelefono());
        if (request.getActivo() != null) sucursal.setActivo(request.getActivo());

        Sucursal updatedSucursal = sucursalRepository.save(sucursal);
        return toSucursalResponse(updatedSucursal);
    }

    @Override
    @Transactional
    public void deleteSucursal(Integer id) {
        if (!sucursalExists(id)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con id: " + id);
        }
        if (canchaService.hasCanchasInSucursal(id)) {
            throw new BusinessRuleException("No se puede eliminar la sucursal con id " + id + " porque tiene canchas asociadas.");
        }
        sucursalRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SucursalResponse activarSucursal(Integer id) {
        Sucursal sucursal = findSucursalEntityById(id);
        sucursal.setActivo(true);
        return toSucursalResponse(sucursalRepository.save(sucursal));
    }

    @Override
    @Transactional
    public SucursalResponse desactivarSucursal(Integer id) {
        Sucursal sucursal = findSucursalEntityById(id);
        sucursal.setActivo(false);
        return toSucursalResponse(sucursalRepository.save(sucursal));
    }

    // Metodos usados internamente por otros servicios
    @Override
    @Transactional(readOnly = true)
    public Sucursal findSucursalEntityById(Integer id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
    }

    @Override
    public boolean hasSucursales(Integer empresaId) {
        return sucursalRepository.existsByEmpresaEmpresaId(empresaId);
    }

    @Override
    public boolean sucursalExists(Integer id) {
        return sucursalRepository.existsById(id);
    }

    private SucursalResponse toSucursalResponse(Sucursal sucursal) {
        return new SucursalResponse(
                sucursal.getSucursalId(),
                sucursal.getEmpresa().getEmpresaId(),
                sucursal.getNombre(),
                sucursal.getDireccion(),
                sucursal.getTelefono(),
                sucursal.isActivo()
        );
    }
}