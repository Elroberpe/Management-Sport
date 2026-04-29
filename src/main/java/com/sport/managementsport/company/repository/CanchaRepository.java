package com.sport.managementsport.company.repository;

import com.sport.managementsport.common.enums.EstadoCancha;
import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.dashboard.dto.SedeStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Integer>, JpaSpecificationExecutor<Cancha> {
    boolean existsBySucursalSucursalId(Integer sucursalId);
    boolean existsByNombreAndSucursalSucursalId(String nombre, Integer sucursalId);
    List<Cancha> findBySucursalSucursalId(Integer sucursalId);

    long countBySucursal_SucursalIdAndEstadoCanchaNot(Integer sucursalId, EstadoCancha estado);
    long countByEstadoCanchaNot(EstadoCancha estado);

    @Query("SELECT s.sucursalId as sucursalId, s.nombre as nombreSede, COUNT(c.canchaId) as canchasTotales, " +
           "SUM(CASE WHEN c.estadoCancha <> com.sport.managementsport.common.enums.EstadoCancha.INACTIVA THEN 1 ELSE 0 END) as canchasActivas " +
           "FROM Cancha c JOIN c.sucursal s " +
           "GROUP BY s.sucursalId, s.nombre")
    List<SedeStatusProjection> getSedesStatus();
}
