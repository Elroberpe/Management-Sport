package com.sport.managementsport.events.repository;

import com.sport.managementsport.events.domain.EventoHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoHorarioRepository extends JpaRepository<EventoHorario, Integer> {
    void deleteByEventoEventoId(Integer eventoId);
}
