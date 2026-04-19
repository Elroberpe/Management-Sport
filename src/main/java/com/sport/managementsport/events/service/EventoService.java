package com.sport.managementsport.events.service;

import com.sport.managementsport.events.dto.CancelEventoRequest;
import com.sport.managementsport.events.dto.CreateEventoRequest;
import com.sport.managementsport.events.dto.EventoResponse;
import com.sport.managementsport.events.dto.UpdateEventoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventoService {
    EventoResponse createEvento(CreateEventoRequest request);
    EventoResponse getEventoById(Integer id);
    Page<EventoResponse> getAllEventos(Pageable pageable);
    EventoResponse updateEvento(Integer id, UpdateEventoRequest request);
    EventoResponse cancelarEvento(Integer id, CancelEventoRequest request);
}
