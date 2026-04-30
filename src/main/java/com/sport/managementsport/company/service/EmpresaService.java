package com.sport.managementsport.company.service;

import com.sport.managementsport.company.domain.Empresa;
import java.util.List;
import java.util.Optional;

public interface EmpresaService {

    Empresa createEmpresa(Empresa empresa);

    Optional<Empresa> getEmpresaById(Integer id);

    List<Empresa> getAllEmpresas();

    Empresa updateEmpresa(Integer id, Empresa empresaDetails);

    void deleteEmpresa(Integer id);
}
