package com.sport.managementsport.company.repository;

import com.sport.managementsport.company.domain.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {
    boolean existsByEmpresaEmpresaId(Integer empresaId);
    List<Sucursal> findByEmpresaEmpresaId(Integer empresaId);
    boolean existsByNombreAndEmpresaEmpresaId(String nombre, Integer empresaId);
    Optional<Sucursal> findByNombreAndEmpresaEmpresaId(String nombre, Integer empresaId);
}
