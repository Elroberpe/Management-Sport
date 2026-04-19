package com.sport.managementsport.finance.repository;

import com.sport.managementsport.finance.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByReservaReservaId(Integer reservaId);
}
