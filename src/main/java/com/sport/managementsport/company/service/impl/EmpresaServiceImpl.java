package com.sport.managementsport.company.service.impl;

import com.sport.managementsport.company.domain.Empresa;
import com.sport.managementsport.company.dto.CreateEmpresaRequest;
import com.sport.managementsport.company.dto.EmpresaResponse;
import com.sport.managementsport.company.dto.UpdateEmpresaRequest;
import com.sport.managementsport.company.repository.EmpresaRepository;
import com.sport.managementsport.company.repository.SucursalRepository;
import com.sport.managementsport.company.service.EmpresaService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.DuplicateResourceException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;

    public EmpresaServiceImpl(EmpresaRepository empresaRepository, SucursalRepository sucursalRepository) {
        this.empresaRepository = empresaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    @Transactional
    public EmpresaResponse createEmpresa(CreateEmpresaRequest request) {
        // 1. Validación de lógica de negocio
        if (empresaRepository.existsByRazonSocial(request.getRazonSocial())) {
            throw new DuplicateResourceException("La razón social '" + request.getRazonSocial() + "' ya está registrada.");
        }

        // 2. Mapeo y transformación de datos
        Empresa empresa = new Empresa();
        empresa.setNombreComercial(request.getNombreComercial().toLowerCase());
        empresa.setRazonSocial(request.getRazonSocial());
        empresa.setEmailContacto(request.getEmailContacto().toLowerCase());
        empresa.setTelefonoPrincipal(request.getTelefonoPrincipal());
        empresa.setLogoUrl(request.getLogoUrl());

        // 3. Guardado en la base de datos
        Empresa savedEmpresa = empresaRepository.save(empresa);

        // 4. Retorno del DTO de respuesta
        return toEmpresaResponse(savedEmpresa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmpresaResponse> getEmpresaById(Integer id) {
        return empresaRepository.findById(id).map(this::toEmpresaResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaResponse> getAllEmpresas() {
        return empresaRepository.findAll().stream()
                .map(this::toEmpresaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmpresaResponse updateEmpresa(Integer id, UpdateEmpresaRequest request) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + id));

        // Actualización parcial
        if (request.getNombreComercial() != null) {
            empresa.setNombreComercial(request.getNombreComercial().toLowerCase());
        }
        if (request.getEmailContacto() != null) {
            empresa.setEmailContacto(request.getEmailContacto().toLowerCase());
        }
        if (request.getTelefonoPrincipal() != null) {
            empresa.setTelefonoPrincipal(request.getTelefonoPrincipal());
        }
        if (request.getLogoUrl() != null) {
            empresa.setLogoUrl(request.getLogoUrl());
        }

        Empresa updatedEmpresa = empresaRepository.save(empresa);
        return toEmpresaResponse(updatedEmpresa);
    }

    @Override
    @Transactional
    public void deleteEmpresa(Integer id) {
        // Primero, verificar que la empresa exista
        if (!empresaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empresa no encontrada con id: " + id);
        }

        // no eliminar empresa si tiene sucursales
        if (sucursalRepository.existsByEmpresaEmpresaId(id)) {
            throw new BusinessRuleException("No se puede eliminar la empresa con id " + id + " porque tiene sucursales asociadas.");
        }

        empresaRepository.deleteById(id);
    }

    // --- Mapper Methods ---
    private EmpresaResponse toEmpresaResponse(Empresa empresa) {
        return new EmpresaResponse(
                empresa.getEmpresaId(),
                empresa.getNombreComercial(),
                empresa.getRazonSocial(),
                empresa.getLogoUrl(),
                empresa.getEmailContacto(),
                empresa.getTelefonoPrincipal()
        );
    }
}
