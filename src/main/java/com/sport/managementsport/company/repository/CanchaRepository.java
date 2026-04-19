package com.sport.managementsport.company.repository;

import com.sport.managementsport.company.domain.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Integer> {
    boolean existsBySucursalSucursalId(Integer sucursalId);
}
