package com.sport.managementsport.company.service.impl;

import com.sport.managementsport.company.domain.Empresa;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.repository.EmpresaRepository;
import com.sport.managementsport.company.repository.SucursalRepository;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;

    public SucursalServiceImpl(SucursalRepository sucursalRepository, EmpresaRepository empresaRepository) {
        this.sucursalRepository = sucursalRepository;
        this.empresaRepository = empresaRepository;
    }

    @Override
    @Transactional
    public SucursalResponse createSucursal(CreateSucursalRequest request) {
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la sucursal. Empresa no encontrada con id: " + request.getEmpresaId()));

        Sucursal sucursal = new Sucursal();
        sucursal.setEmpresa(empresa);
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setActivo(true); // Por defecto, una nueva sucursal está activa

        Sucursal savedSucursal = sucursalRepository.save(sucursal);
        return toSucursalResponse(savedSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SucursalResponse> getSucursalById(Integer id) {
        return sucursalRepository.findById(id).map(this::toSucursalResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalResponse> getAllSucursales() {
        return sucursalRepository.findAll().stream()
                .map(this::toSucursalResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSucursal(Integer id) {
        if (!sucursalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con id: " + id);
        }
        // Aquí podríamos añadir una regla de negocio para no borrar sucursales con canchas
        sucursalRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalResponse> getSucursalesByEmpresaId(Integer empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResourceNotFoundException("Empresa no encontrada con id: " + empresaId);
        }
        return sucursalRepository.findByEmpresaEmpresaId(empresaId).stream()
                .map(this::toSucursalResponse)
                .collect(Collectors.toList());
    }

    // --- Mapper Method ---
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
