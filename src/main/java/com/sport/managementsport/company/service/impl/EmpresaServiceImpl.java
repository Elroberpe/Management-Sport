package com.sport.managementsport.company.service.impl;

import com.sport.managementsport.company.domain.Empresa;
import com.sport.managementsport.company.dto.CreateEmpresaRequest;
import com.sport.managementsport.company.dto.EmpresaResponse;
import com.sport.managementsport.company.dto.UpdateEmpresaRequest;
import com.sport.managementsport.company.repository.EmpresaRepository;
import com.sport.managementsport.company.service.EmpresaService;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.DuplicateResourceException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final SucursalService sucursalService;

    public EmpresaServiceImpl(EmpresaRepository empresaRepository, SucursalService sucursalService) {
        this.empresaRepository = empresaRepository;
        this.sucursalService = sucursalService;
    }

    @Override
    @Transactional
    public EmpresaResponse createEmpresa(CreateEmpresaRequest request) {
        if (empresaRepository.existsByRazonSocial(request.getRazonSocial())) {
            throw new DuplicateResourceException("La razón social '" + request.getRazonSocial() + "' ya está en uso.");
        }
        if (empresaRepository.existsByNombreComercial(request.getNombreComercial())) {
            throw new DuplicateResourceException("El nombre comercial '" + request.getNombreComercial() + "' ya está en uso.");
        }

        Empresa empresa = new Empresa();
        empresa.setNombreComercial(request.getNombreComercial());
        empresa.setRazonSocial(request.getRazonSocial());
        empresa.setLogoUrl(request.getLogoUrl());
        empresa.setEmailContacto(request.getEmailContacto());
        empresa.setTelefonoPrincipal(request.getTelefonoPrincipal());

        Empresa savedEmpresa = empresaRepository.save(empresa);
        return toEmpresaResponse(savedEmpresa);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponse getEmpresaById(Integer id) {
        return empresaRepository.findById(id)
                .map(this::toEmpresaResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + id));
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

        if (request.getNombreComercial() != null) {
            empresaRepository.findByNombreComercial(request.getNombreComercial()).ifPresent(e -> {
                if (!e.getEmpresaId().equals(id)) {
                    throw new DuplicateResourceException("El nombre comercial '" + request.getNombreComercial() + "' ya está en uso.");
                }
            });
            empresa.setNombreComercial(request.getNombreComercial());
        }

        if (request.getLogoUrl() != null) empresa.setLogoUrl(request.getLogoUrl());
        if (request.getEmailContacto() != null) empresa.setEmailContacto(request.getEmailContacto());
        if (request.getTelefonoPrincipal() != null) empresa.setTelefonoPrincipal(request.getTelefonoPrincipal());

        Empresa updatedEmpresa = empresaRepository.save(empresa);
        return toEmpresaResponse(updatedEmpresa);
    }

    @Override
    @Transactional
    public void deleteEmpresa(Integer id) {
        if (!empresaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empresa no encontrada con id: " + id);
        }
        if (sucursalService.hasSucursales(id)) {
            throw new BusinessRuleException("No se puede eliminar la empresa con id " + id + " porque tiene sucursales asociadas.");
        }
        empresaRepository.deleteById(id);
    }

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
