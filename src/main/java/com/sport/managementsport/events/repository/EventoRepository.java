package com.sport.managementsport.events.repository;

import com.sport.managementsport.common.enums.EstadoEvento;
import com.sport.managementsport.events.domain.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {
    Page<Evento> findBySucursalSucursalId(Integer sucursalId, Pageable pageable);
    List<Evento> findByEstado(EstadoEvento estado);
}
