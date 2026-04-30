package com.sport.managementsport.company.service.impl;

import com.sport.managementsport.company.domain.Empresa;
import com.sport.managementsport.company.repository.EmpresaRepository;
import com.sport.managementsport.company.service.EmpresaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaServiceImpl(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Override
    @Transactional
    public Empresa createEmpresa(Empresa empresa) {
        // Aquí se podrían añadir validaciones antes de guardar
        return empresaRepository.save(empresa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> getEmpresaById(Integer id) {
        return empresaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empresa> getAllEmpresas() {
        return empresaRepository.findAll();
    }

    @Override
    @Transactional
    public Empresa updateEmpresa(Integer id, Empresa empresaDetails) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + id));

        empresa.setNombreComercial(empresaDetails.getNombreComercial());
        empresa.setRazonSocial(empresaDetails.getRazonSocial());
        empresa.setLogoUrl(empresaDetails.getLogoUrl());
        empresa.setEmailContacto(empresaDetails.getEmailContacto());
        empresa.setTelefonoPrincipal(empresaDetails.getTelefonoPrincipal());
        // Los campos de auditoría se manejarían con @PreUpdate si se configura un listener

        return empresaRepository.save(empresa);
    }

    @Override
    @Transactional
    public void deleteEmpresa(Integer id) {
        if (!empresaRepository.existsById(id)) {
            throw new RuntimeException("Empresa no encontrada con id: " + id);
        }
        empresaRepository.deleteById(id);
    }
}
