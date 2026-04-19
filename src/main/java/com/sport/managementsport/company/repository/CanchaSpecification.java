package com.sport.managementsport.company.repository;

import com.sport.managementsport.common.enums.EstadoCancha;
import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.company.domain.Sucursal;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

public class CanchaSpecification {

    public static Specification<Cancha> sucursalIdEquals(Integer sucursalId) {
        return (root, query, criteriaBuilder) -> {
            if (sucursalId == null) {
                return criteriaBuilder.conjunction(); // Devuelve una condición "verdadera" si no hay filtro
            }
            Join<Cancha, Sucursal> sucursalJoin = root.join("sucursal");
            return criteriaBuilder.equal(sucursalJoin.get("sucursalId"), sucursalId);
        };
    }

    public static Specification<Cancha> estadoEquals(EstadoCancha estado) {
        return (root, query, criteriaBuilder) -> {
            if (estado == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("estadoCancha"), estado);
        };
    }
}
