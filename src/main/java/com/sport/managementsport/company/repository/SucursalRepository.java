package com.sport.managementsport.company.repository;

import com.sport.managementsport.company.domain.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {
    boolean existsByEmpresaEmpresaId(Integer empresaId);
    List<Sucursal> findByEmpresaEmpresaId(Integer empresaId);
}
