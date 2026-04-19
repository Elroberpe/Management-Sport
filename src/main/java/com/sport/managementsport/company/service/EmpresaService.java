package com.sport.managementsport.company.service;

import com.sport.managementsport.company.dto.CreateEmpresaRequest;
import com.sport.managementsport.company.dto.EmpresaResponse;
import com.sport.managementsport.company.dto.UpdateEmpresaRequest;

import java.util.List;
import java.util.Optional;

public interface EmpresaService {

    EmpresaResponse createEmpresa(CreateEmpresaRequest request);

    Optional<EmpresaResponse> getEmpresaById(Integer id);

    List<EmpresaResponse> getAllEmpresas();

    EmpresaResponse updateEmpresa(Integer id, UpdateEmpresaRequest request);

    void deleteEmpresa(Integer id);
}
