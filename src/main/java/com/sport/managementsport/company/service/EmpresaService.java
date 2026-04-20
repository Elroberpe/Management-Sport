package com.sport.managementsport.company.service;

import com.sport.managementsport.company.domain.Empresa;
import com.sport.managementsport.company.dto.CreateEmpresaRequest;
import com.sport.managementsport.company.dto.EmpresaResponse;
import com.sport.managementsport.company.dto.UpdateEmpresaRequest;

import java.util.List;

public interface EmpresaService {
    EmpresaResponse createEmpresa(CreateEmpresaRequest request);
    EmpresaResponse getEmpresaById(Integer id);
    List<EmpresaResponse> getAllEmpresas();
    EmpresaResponse updateEmpresa(Integer id, UpdateEmpresaRequest request);
    void deleteEmpresa(Integer id);
    boolean empresaExists(Integer id);

    // Nuevo método para uso interno
    Empresa findEmpresaEntityById(Integer id);
}
