package com.sport.managementsport.finance.repository;

import com.sport.managementsport.common.enums.MetodoPago;
import com.sport.managementsport.finance.domain.Pago;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PagoSpecification {

    public static Specification<Pago> fechaBetween(LocalDate desde, LocalDate hasta) {
        return (root, query, cb) -> {
            if (desde != null && hasta != null) {
                return cb.between(root.get("fecha"), desde, hasta);
            }
            if (desde != null) {
                return cb.greaterThanOrEqualTo(root.get("fecha"), desde);
            }
            if (hasta != null) {
                return cb.lessThanOrEqualTo(root.get("fecha"), hasta);
            }
            return cb.conjunction();
        };
    }

    public static Specification<Pago> metodoPagoEquals(MetodoPago metodo) {
        return (root, query, cb) -> metodo == null ? cb.conjunction() : cb.equal(root.get("metodoPago"), metodo);
    }
}
