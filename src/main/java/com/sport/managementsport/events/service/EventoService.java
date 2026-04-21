package com.sport.managementsport.events.service;

import com.sport.managementsport.events.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface EventoService {
    EventoResponse createEvento(CreateEventoRequest request);
    EventoResponse getEventoById(Integer id);
    Page<EventoResponse> getAllEventos(Pageable pageable);
    EventoResponse updateEvento(Integer id, UpdateEventoRequest request);
    EventoResponse cancelarEvento(Integer id, CancelEventoRequest request);
    EventoResponse addPago(Integer id, AddPagoToEventoRequest request);
    EventoResponse reprogramarEvento(Integer id, ReprogramarEventoRequest request);

    // Nuevo método para uso interno
    void revertirSaldosPorAnulacion(Integer eventoId, BigDecimal montoAnulado);
}
