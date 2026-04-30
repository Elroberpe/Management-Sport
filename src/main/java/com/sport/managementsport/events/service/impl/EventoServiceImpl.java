package com.sport.managementsport.events.service.impl;

import com.sport.managementsport.events.domain.Evento;
import com.sport.managementsport.events.repository.EventoRepository;
import com.sport.managementsport.events.service.EventoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;

    public EventoServiceImpl(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    @Transactional
    public Evento createEvento(Evento evento) {
        // Aquí se podrían añadir validaciones
        return eventoRepository.save(evento);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Evento> getEventoById(Integer id) {
        return eventoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evento> getAllEventos() {
        return eventoRepository.findAll();
    }

    @Override
    @Transactional
    public Evento updateEvento(Integer id, Evento eventoDetails) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + id));

        evento.setNombre(eventoDetails.getNombre());
        evento.setDescripcion(eventoDetails.getDescripcion());
        evento.setTipoEvento(eventoDetails.getTipoEvento());
        evento.setMontoPactado(eventoDetails.getMontoPactado());
        evento.setFechaInicio(eventoDetails.getFechaInicio());
        evento.setFechaFin(eventoDetails.getFechaFin());
        evento.setEstado(eventoDetails.getEstado());

        return eventoRepository.save(evento);
    }

    @Override
    @Transactional
    public void deleteEvento(Integer id) {
        if (!eventoRepository.existsById(id)) {
            throw new RuntimeException("Evento no encontrado con id: " + id);
        }
        eventoRepository.deleteById(id);
    }
}
