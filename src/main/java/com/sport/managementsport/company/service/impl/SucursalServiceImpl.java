package com.sport.managementsport.company.service.impl;

import com.sport.managementsport.company.domain.Empresa;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.dto.UpdateSucursalRequest;
import com.sport.managementsport.company.repository.CanchaRepository;
import com.sport.managementsport.company.repository.EmpresaRepository;
import com.sport.managementsport.company.repository.SucursalRepository;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.DuplicateResourceException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;
    private final CanchaRepository canchaRepository;

    @Override
    @Transactional
    public SucursalResponse createSucursal(CreateSucursalRequest request) {
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la sucursal. Empresa no encontrada con id: " + request.getEmpresaId()));

        if (sucursalRepository.existsByNombreAndEmpresaEmpresaId(request.getNombre(), request.getEmpresaId())) {
            throw new DuplicateResourceException("Ya existe una sucursal con el nombre '" + request.getNombre() + "' en esta empresa.");
        }

        Sucursal sucursal = new Sucursal();
        sucursal.setEmpresa(empresa);
        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setActivo(true);

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
    public SucursalResponse updateSucursal(Integer id, UpdateSucursalRequest request) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));

        if (request.getNombre() != null) {
            sucursalRepository.findByNombreAndEmpresaEmpresaId(request.getNombre(), sucursal.getEmpresa().getEmpresaId())
                    .ifPresent(existingSucursal -> {
                        if (!existingSucursal.getSucursalId().equals(id)) {
                            throw new DuplicateResourceException("El nombre '" + request.getNombre() + "' ya está en uso por otra sucursal en esta empresa.");
                        }
                    });
            sucursal.setNombre(request.getNombre());
        }

        if (request.getDireccion() != null) {
            sucursal.setDireccion(request.getDireccion());
        }
        if (request.getTelefono() != null) {
            sucursal.setTelefono(request.getTelefono());
        }
        if (request.getActivo() != null) {
            sucursal.setActivo(request.getActivo());
        }

        Sucursal updatedSucursal = sucursalRepository.save(sucursal);
        return toSucursalResponse(updatedSucursal);
    }

    @Override
    @Transactional
    public void deleteSucursal(Integer id) {
        if (!sucursalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con id: " + id);
        }

        if (canchaRepository.existsBySucursalSucursalId(id)) {
            throw new BusinessRuleException("No se puede eliminar la sucursal con id " + id + " porque tiene canchas asociadas.");
        }
        
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

    @Override
    @Transactional
    public SucursalResponse activarSucursal(Integer id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
        sucursal.setActivo(true);
        Sucursal savedSucursal = sucursalRepository.save(sucursal);
        return toSucursalResponse(savedSucursal);
    }

    @Override
    @Transactional
    public SucursalResponse desactivarSucursal(Integer id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
        sucursal.setActivo(false);
        Sucursal savedSucursal = sucursalRepository.save(sucursal);
        return toSucursalResponse(savedSucursal);
    }

    @Override
    public Sucursal findSucursalEntityById(Integer id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
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
