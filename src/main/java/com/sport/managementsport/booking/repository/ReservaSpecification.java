package com.sport.managementsport.booking.repository;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.common.enums.EstadoReserva;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ReservaSpecification {

    public static Specification<Reserva> fechaEquals(LocalDate fecha) {
        return (root, query, criteriaBuilder) ->
                fecha == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("fecha"), fecha);
    }

    public static Specification<Reserva> canchaIdEquals(Integer canchaId) {
        return (root, query, criteriaBuilder) ->
                canchaId == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("cancha").get("canchaId"), canchaId);
    }

    public static Specification<Reserva> clienteIdEquals(Integer clienteId) {
        return (root, query, criteriaBuilder) ->
                clienteId == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("cliente").get("clienteId"), clienteId);
    }

    public static Specification<Reserva> estadoEquals(EstadoReserva estado) {
        return (root, query, criteriaBuilder) ->
                estado == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("estadoReserva"), estado);
    }
}
