package com.sport.managementsport.events.repository;

import com.sport.managementsport.common.enums.EstadoMantenimiento;
import com.sport.managementsport.dashboard.dto.HorasOcupadasPorDia;
import com.sport.managementsport.events.domain.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Integer>, JpaSpecificationExecutor<Mantenimiento> {

    @Query("SELECT m FROM Mantenimiento m WHERE m.cancha.canchaId = :canchaId AND m.horaInicio < :horaFin AND m.horaFin > :horaInicio AND m.estadoMantenimiento <> 'CANCELADO'")
    List<Mantenimiento> findConflictingMantenimientos(
            @Param("canchaId") Integer canchaId,
            @Param("horaInicio") LocalDateTime horaInicio,
            @Param("horaFin") LocalDateTime horaFin
    );

    @Query("SELECT m FROM Mantenimiento m WHERE m.cancha.canchaId = :canchaId AND m.mantenimientoId <> :mantenimientoIdToIgnore AND m.horaInicio < :horaFin AND m.horaFin > :horaInicio AND m.estadoMantenimiento <> 'CANCELADO'")
    List<Mantenimiento> findConflictingMantenimientosIgnoringSelf(
            @Param("canchaId") Integer canchaId,
            @Param("horaInicio") LocalDateTime horaInicio,
            @Param("horaFin") LocalDateTime horaFin,
            @Param("mantenimientoIdToIgnore") Integer mantenimientoIdToIgnore
    );

    List<Mantenimiento> findByCanchaCanchaId(Integer canchaId);

    List<Mantenimiento> findByEstadoMantenimientoAndHoraInicioBefore(EstadoMantenimiento estado, LocalDateTime ahora);

    List<Mantenimiento> findByEstadoMantenimientoAndHoraFinBefore(EstadoMantenimiento estado, LocalDateTime ahora);

    @Query(value = "SELECT CAST(m.hora_inicio AS DATE) as dia, SUM(DATEDIFF(minute, m.hora_inicio, m.hora_fin) / 60.0) as horasOcupadas " +
                   "FROM Mantenimiento m JOIN Cancha c ON m.cancha_id = c.cancha_id " +
                   "WHERE c.sucursal_id = :sucursalId AND CAST(m.hora_inicio AS DATE) BETWEEN :fechaDesde AND :fechaHasta " +
                   "AND m.estado_mantenimiento <> 'CANCELADO' " +
                   "GROUP BY CAST(m.hora_inicio AS DATE)", nativeQuery = true)
    List<HorasOcupadasPorDia> findHorasOcupadasPorSucursalYFechas(@Param("sucursalId") Integer sucursalId, @Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);

    @Query(value = "SELECT COALESCE(SUM(DATEDIFF(minute, m.hora_inicio, m.hora_fin) / 60.0), 0) " +
                   "FROM Mantenimiento m " +
                   "WHERE CAST(m.hora_inicio AS DATE) BETWEEN :fechaDesde AND :fechaHasta " +
                   "AND m.estado_mantenimiento <> 'CANCELADO'", nativeQuery = true)
    BigDecimal sumHorasOcupadasByFechas(@Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);
}
