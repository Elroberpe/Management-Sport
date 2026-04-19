package com.sport.managementsport.company.repository;

import com.sport.managementsport.company.domain.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Integer>, JpaSpecificationExecutor<Cancha> {
    boolean existsBySucursalSucursalId(Integer sucursalId);
    boolean existsByNombreAndSucursalSucursalId(String nombre, Integer sucursalId);
    List<Cancha> findBySucursalSucursalId(Integer sucursalId);
}
