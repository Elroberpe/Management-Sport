package com.sport.managementsport.identity.repository;

import com.sport.managementsport.identity.domain.Cliente;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ClienteSpecification {

    public static Specification<Cliente> documentoContains(String documento) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(documento)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("documento"), "%" + documento + "%");
        };
    }

    public static Specification<Cliente> nombreContains(String nombre) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(nombre)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
        };
    }

    public static Specification<Cliente> searchByQuery(String query) {
        return (root, q, cb) -> {
            if (!StringUtils.hasText(query)) {
                return cb.conjunction();
            }
            String likePattern = "%" + query.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("nombre")), likePattern),
                cb.like(root.get("documento"), likePattern)
            );
        };
    }
}
