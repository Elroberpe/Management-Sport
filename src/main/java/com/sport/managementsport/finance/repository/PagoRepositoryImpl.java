package com.sport.managementsport.finance.repository;

import com.sport.managementsport.common.enums.MetodoPago;
import com.sport.managementsport.finance.dto.PagoStatsResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PagoRepositoryImpl implements PagoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public PagoStatsResponse getPagoStats(LocalDate desde, LocalDate hasta, MetodoPago metodo, Integer sucursalId) {
        // Base de la consulta sin la parte de WHERE dinámico
        String baseJpql = "SELECT new com.sport.managementsport.finance.dto.PagoStatsResponse(" +
                "COALESCE(SUM(CASE WHEN p.tipoTransaccion = 'INGRESO' AND p.estado <> com.sport.managementsport.common.enums.EstadoPago.ANULADO THEN p.monto ELSE 0 END), 0), " +
                "COALESCE(SUM(CASE WHEN p.tipoTransaccion = 'SALIDA' AND p.estado <> com.sport.managementsport.common.enums.EstadoPago.ANULADO THEN p.monto ELSE 0 END), 0), " +
                "COALESCE(SUM(CASE WHEN p.estado = com.sport.managementsport.common.enums.EstadoPago.ANULADO THEN p.monto ELSE 0 END), 0), " +
                "COALESCE(COUNT(CASE WHEN p.tipoTransaccion = 'INGRESO' AND p.estado <> com.sport.managementsport.common.enums.EstadoPago.ANULADO THEN 1 END), 0L), " +
                "COALESCE(COUNT(CASE WHEN p.tipoTransaccion = 'SALIDA' AND p.estado <> com.sport.managementsport.common.enums.EstadoPago.ANULADO THEN 1 END), 0L), " +
                "COALESCE(COUNT(CASE WHEN p.estado = com.sport.managementsport.common.enums.EstadoPago.ANULADO THEN 1 END), 0L)) " +
                "FROM Pago p";

        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        if (sucursalId != null) {
            // Si se filtra por sucursal, es necesario hacer JOIN.
            // Esto asegura que solo los pagos vinculados a una reserva con cancha y sucursal se consideren.
            baseJpql += " JOIN p.reserva r JOIN r.cancha c JOIN c.sucursal s";
            whereClause.append(" AND s.sucursalId = :sucursalId");
            parameters.put("sucursalId", sucursalId);
        }

        if (desde != null) {
            whereClause.append(" AND p.fecha >= :desde");
            parameters.put("desde", desde);
        }
        if (hasta != null) {
            whereClause.append(" AND p.fecha <= :hasta");
            parameters.put("hasta", hasta);
        }
        if (metodo != null) {
            whereClause.append(" AND p.metodoPago = :metodo");
            parameters.put("metodo", metodo);
        }

        String finalJpql = baseJpql + whereClause.toString();
        TypedQuery<PagoStatsResponse> query = em.createQuery(finalJpql, PagoStatsResponse.class);
        parameters.forEach(query::setParameter);

        return query.getSingleResult();
    }
}
