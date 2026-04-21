package com.sport.managementsport.booking.repository;

import com.sport.managementsport.booking.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer>, JpaSpecificationExecutor<Reserva> {

    // Consulta corregida para SQL Server usando una consulta nativa
    @Query(value = "SELECT * FROM Reserva r WHERE r.cancha_id = :canchaId AND r.estado_reserva <> 'CANCELADO' AND " +
                   "DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_inicio), CAST(r.fecha AS DATETIME2)) < :endDateTime AND " +
                   ":startDateTime < DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_fin), CAST(r.fecha AS DATETIME2))",
           nativeQuery = true)
    List<Reserva> findConflictingReservas(
            @Param("canchaId") Integer canchaId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // Consulta corregida para SQL Server usando una consulta nativa
    @Query(value = "SELECT * FROM Reserva r WHERE r.cancha_id = :canchaId AND r.estado_reserva <> 'CANCELADO' AND " +
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

    // Consulta de la tarea programada (ya corregida previamente)
    @Query(value = "SELECT * FROM Reserva r WHERE r.estado_reserva = :estado AND DATEADD(ms, DATEDIFF(ms, '00:00:00', r.hora_fin), CAST(r.fecha AS DATETIME2)) < :currentDateTime", nativeQuery = true)
    List<Reserva> findReservasToUpdateStatus(
            @Param("estado") String estado,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );
}
