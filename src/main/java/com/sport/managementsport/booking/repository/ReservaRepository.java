package com.sport.managementsport.booking.repository;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.common.enums.EstadoReserva;
import com.sport.managementsport.dashboard.dto.ActividadPorDia;
import com.sport.managementsport.dashboard.dto.HorasOcupadasPorDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer>, JpaSpecificationExecutor<Reserva> {

    @Query(value = "SELECT * FROM Reserva r WHERE r.cancha_id = :canchaId AND r.estado_reserva IN ('PENDIENTE', 'PAGADA') AND " +
                   "DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_inicio), CAST(r.fecha AS DATETIME2)) < :endDateTime AND " +
                   ":startDateTime < DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_fin), CAST(r.fecha AS DATETIME2))",
           nativeQuery = true)
    List<Reserva> findConflictingReservas(
            @Param("canchaId") Integer canchaId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query(value = "SELECT * FROM Reserva r WHERE r.cancha_id = :canchaId AND r.reserva_id <> :reservaIdToIgnore AND r.estado_reserva IN ('PENDIENTE', 'PAGADA') AND " +
                   "DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_inicio), CAST(r.fecha AS DATETIME2)) < :endDateTime AND " +
                   ":startDateTime < DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_fin), CAST(r.fecha AS DATETIME2))",
           nativeQuery = true)
    List<Reserva> findConflictingReservasIgnoringSelf(
            @Param("canchaId") Integer canchaId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("reservaIdToIgnore") Integer reservaIdToIgnore
    );

    @Query(value = "SELECT * FROM Reserva r WHERE r.cancha_id = :canchaId AND r.estado_reserva IN ('PENDIENTE', 'PAGADA') AND " +
                   "(r.evento_id IS NULL OR r.evento_id <> :eventoIdToIgnore) AND " +
                   "DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_inicio), CAST(r.fecha AS DATETIME2)) < :endDateTime AND " +
                   ":startDateTime < DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_fin), CAST(r.fecha AS DATETIME2))",
           nativeQuery = true)
    List<Reserva> findConflictingReservasIgnoringEvent(
            @Param("canchaId") Integer canchaId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("eventoIdToIgnore") Integer eventoIdToIgnore
    );

    @Query(value = "SELECT * FROM Reserva r WHERE r.estado_reserva = :estado AND DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_fin), CAST(r.fecha AS DATETIME2)) < :currentDateTime", nativeQuery = true)
    List<Reserva> findReservasToUpdateStatus(
            @Param("estado") String estado,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );

    @Query(value = "SELECT CAST(r.fecha AS DATE) as dia, SUM(DATEDIFF(minute, r.hora_inicio, r.hora_fin) / 60.0) as horasOcupadas " +
                   "FROM Reserva r JOIN Cancha c ON r.cancha_id = c.cancha_id " +
                   "WHERE c.sucursal_id = :sucursalId AND r.fecha BETWEEN :fechaDesde AND :fechaHasta " +
                   "AND r.estado_reserva NOT IN ('CANCELADO', 'REEMBOLSADO') " +
                   "GROUP BY CAST(r.fecha AS DATE)", nativeQuery = true)
    List<HorasOcupadasPorDia> findHorasOcupadasPorSucursalYFechas(@Param("sucursalId") Integer sucursalId, @Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);

    long countByEstadoReservaAndFecha(EstadoReserva estado, LocalDate fecha);

    @Query("SELECT count(r) FROM Reserva r WHERE r.cancha.sucursal.sucursalId = :sucursalId AND r.estadoReserva = :estado AND r.fecha = :fecha")
    long countBySucursalAndEstadoAndFecha(@Param("sucursalId") Integer sucursalId, @Param("estado") EstadoReserva estado, @Param("fecha") LocalDate fecha);

    @Query(value = "SELECT COALESCE(SUM(DATEDIFF(minute, r.hora_inicio, r.hora_fin) / 60.0), 0) " +
                   "FROM Reserva r " +
                   "WHERE r.fecha BETWEEN :fechaDesde AND :fechaHasta " +
                   "AND r.estado_reserva NOT IN ('CANCELADO', 'REEMBOLSADO')", nativeQuery = true)
    BigDecimal sumHorasOcupadasByFechas(@Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);

    @Query(value = "SELECT r.fecha as fecha, COUNT(r.reserva_id) as cantidad FROM Reserva r " +
                   "WHERE r.fecha BETWEEN :fechaDesde AND :fechaHasta " +
                   "AND r.estado_reserva IN :estados " +
                   "GROUP BY r.fecha ORDER BY r.fecha", nativeQuery = true)
    List<ActividadPorDia> countActividadPorDia(@Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta, @Param("estados") Collection<String> estados);

    @Query(value = "SELECT r.fecha as fecha, COUNT(r.reserva_id) as cantidad FROM Reserva r " +
                   "JOIN Cancha c ON r.cancha_id = c.cancha_id " +
                   "WHERE c.sucursal_id = :sucursalId AND r.fecha BETWEEN :fechaDesde AND :fechaHasta " +
                   "AND r.estado_reserva IN :estados " +
                   "GROUP BY r.fecha ORDER BY r.fecha", nativeQuery = true)
    List<ActividadPorDia> countActividadPorDiaYPorSucursal(@Param("sucursalId") Integer sucursalId, @Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta, @Param("estados") Collection<String> estados);
}
