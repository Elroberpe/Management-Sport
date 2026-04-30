package com.sport.managementsport.events.repository;

import com.sport.managementsport.events.domain.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Integer> {
}
