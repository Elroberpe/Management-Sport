package com.sport.managementsport.finance.repository;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.common.enums.MetodoPago;
import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.events.domain.Evento;
import com.sport.managementsport.finance.domain.Pago;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
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

    public static Specification<Pago> sucursalIdEquals(Integer sucursalId) {
        return (root, query, cb) -> {
            if (sucursalId == null) {
                return cb.conjunction();
            }

            // Join para pagos de Reservas
            Join<Pago, Reserva> reservaJoin = root.join("reserva", jakarta.persistence.criteria.JoinType.LEFT);
            Join<Reserva, Cancha> canchaJoin = reservaJoin.join("cancha", jakarta.persistence.criteria.JoinType.LEFT);
            Join<Cancha, Sucursal> sucursalFromReservaJoin = canchaJoin.join("sucursal", jakarta.persistence.criteria.JoinType.LEFT);
            Predicate fromReserva = cb.equal(sucursalFromReservaJoin.get("sucursalId"), sucursalId);

            // Join para pagos de Eventos
            Join<Pago, Evento> eventoJoin = root.join("evento", jakarta.persistence.criteria.JoinType.LEFT);
            Join<Evento, Sucursal> sucursalFromEventoJoin = eventoJoin.join("sucursal", jakarta.persistence.criteria.JoinType.LEFT);
            Predicate fromEvento = cb.equal(sucursalFromEventoJoin.get("sucursalId"), sucursalId);

            // Unir ambas condiciones con un OR
            return cb.or(fromReserva, fromEvento);
        };
    }
}
