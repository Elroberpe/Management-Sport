package com.sport.managementsport.events.repository;

import com.sport.managementsport.events.domain.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Integer> {

    /**
     * Busca mantenimientos que se solapen con un intervalo de tiempo dado para una cancha específica.
     * La lógica de solapamiento es: (StartA < EndB) and (EndA > StartB)
     */
    @Query("SELECT m FROM Mantenimiento m WHERE m.cancha.canchaId = :canchaId AND m.horaInicio < :horaFin AND m.horaFin > :horaInicio AND m.estadoMantenimiento <> 'CANCELADO'")
    List<Mantenimiento> findConflictingMantenimientos(
            @Param("canchaId") Integer canchaId,
            @Param("horaInicio") LocalDateTime horaInicio,
            @Param("horaFin") LocalDateTime horaFin
    );
}
