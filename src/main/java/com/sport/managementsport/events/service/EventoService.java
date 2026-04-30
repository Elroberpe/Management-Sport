package com.sport.managementsport.events.service;

import com.sport.managementsport.events.domain.Evento;
import java.util.List;
import java.util.Optional;

public interface EventoService {

    Evento createEvento(Evento evento);

    Optional<Evento> getEventoById(Integer id);

    List<Evento> getAllEventos();

    Evento updateEvento(Integer id, Evento eventoDetails);

    void deleteEvento(Integer id);
}
