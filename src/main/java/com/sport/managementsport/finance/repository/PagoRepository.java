package com.sport.managementsport.finance.repository;

import com.sport.managementsport.common.enums.TipoTransaccion;
import com.sport.managementsport.finance.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer>, JpaSpecificationExecutor<Pago> {
    List<Pago> findByReservaReservaId(Integer reservaId);
    List<Pago> findByEventoEventoId(Integer eventoId);

    // --- SQL Server / Forma Antigua (Comentado) ---
    // @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.tipoTransaccion = :tipo AND YEAR(p.fecha) = :year")
    // --- Forma Estándar JPA / PostgreSQL ---
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.tipoTransaccion = :tipo AND EXTRACT(YEAR FROM p.fecha) = :year")
    BigDecimal sumByTipoTransaccionAndYear(@Param("tipo") TipoTransaccion tipo, @Param("year") int year);

    // --- SQL Server / Forma Antigua (Comentado) ---
    // @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p " +
    //        "LEFT JOIN p.reserva r LEFT JOIN r.cancha c " +
    //        "WHERE p.tipoTransaccion = :tipo AND YEAR(p.fecha) = :year AND c.sucursal.sucursalId = :sucursalId")
    // --- Forma Estándar JPA / PostgreSQL ---
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p " +
           "LEFT JOIN p.reserva r LEFT JOIN r.cancha c " +
           "WHERE p.tipoTransaccion = :tipo AND EXTRACT(YEAR FROM p.fecha) = :year AND c.sucursal.sucursalId = :sucursalId")
    BigDecimal sumByTipoTransaccionAndYearAndSucursal(@Param("tipo") TipoTransaccion tipo, @Param("year") int year, @Param("sucursalId") Integer sucursalId);
}