package com.sport.managementsport.booking.repository;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.common.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer>, JpaSpecificationExecutor<Reserva> {

    @Query("SELECT r FROM Reserva r WHERE r.cancha.canchaId = :canchaId AND r.fecha = :fecha AND r.horaInicio < :horaFin AND r.horaFin > :horaInicio AND r.estadoReserva <> 'CANCELADO'")
    List<Reserva> findConflictingReservas(
            @Param("canchaId") Integer canchaId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );

    @Query("SELECT r FROM Reserva r WHERE r.estadoReserva = :estado AND (r.fecha < :fechaActual OR (r.fecha = :fechaActual AND r.horaFin < :horaActual))")
    List<Reserva> findReservasToUpdateStatus(
            @Param("estado") EstadoReserva estado,
            @Param("fechaActual") LocalDate fechaActual,
            @Param("horaActual") LocalTime horaActual
    );
}
