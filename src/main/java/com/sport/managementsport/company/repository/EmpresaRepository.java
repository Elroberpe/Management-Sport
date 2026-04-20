package com.sport.managementsport.company.repository;

import com.sport.managementsport.company.domain.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    boolean existsByRazonSocial(String razonSocial);
    boolean existsByNombreComercial(String nombreComercial);
    Optional<Empresa> findByNombreComercial(String nombreComercial);
}
