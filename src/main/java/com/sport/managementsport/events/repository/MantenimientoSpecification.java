package com.sport.managementsport.events.repository;

import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.events.domain.Mantenimiento;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class MantenimientoSpecification {

    public static Specification<Mantenimiento> sucursalIdEquals(Integer sucursalId) {
        return (root, query, criteriaBuilder) -> {
            if (sucursalId == null) {
                return criteriaBuilder.conjunction();
            }
            // Join: Mantenimiento -> Cancha -> Sucursal
            Join<Mantenimiento, Cancha> canchaJoin = root.join("cancha");
            Join<Cancha, Sucursal> sucursalJoin = canchaJoin.join("sucursal");
            return criteriaBuilder.equal(sucursalJoin.get("sucursalId"), sucursalId);
        };
    }
}
