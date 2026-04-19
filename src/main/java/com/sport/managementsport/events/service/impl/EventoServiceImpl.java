package com.sport.managementsport.events.service.impl;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.booking.dto.CancelReservaRequest;
import com.sport.managementsport.booking.service.ReservaService;
import com.sport.managementsport.common.enums.EstadoEvento;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.events.domain.Evento;
import com.sport.managementsport.events.dto.CancelEventoRequest;
import com.sport.managementsport.events.dto.CreateEventoRequest;
import com.sport.managementsport.events.dto.EventoResponse;
import com.sport.managementsport.events.dto.UpdateEventoRequest;
import com.sport.managementsport.events.repository.EventoRepository;
import com.sport.managementsport.events.service.EventoService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import com.sport.managementsport.identity.domain.Cliente;
import com.sport.managementsport.identity.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final SucursalService sucursalService;

    @Override
    @Transactional
    public EventoResponse createEvento(CreateEventoRequest request) {
        Sucursal sucursal = sucursalService.findSucursalEntityById(request.getSucursalId());
        Cliente cliente = clienteService.findClienteEntityById(request.getClienteId());

        for (CreateEventoRequest.HorarioBloqueoDto horario : request.getHorarios()) {
            reservaService.validateHorarioDisponible(horario.getCanchaId(), horario.getFecha(), horario.getHoraInicio(), horario.getHoraFin());
        }

        Evento evento = new Evento();
        evento.setSucursal(sucursal);
        evento.setCliente(cliente);
        evento.setNombre(request.getNombre());
        evento.setDescripcion(request.getDescripcion());
        evento.setTipoEvento(request.getTipoEvento());
        evento.setMontoPactado(request.getMontoPactado());
        evento.setFechaInicio(request.getFechaInicio());
        evento.setFechaFin(request.getFechaFin());
        
        Evento savedEvento = eventoRepository.save(evento);

        List<Reserva> reservasCreadas = new ArrayList<>();
        for (CreateEventoRequest.HorarioBloqueoDto horario : request.getHorarios()) {
            Reserva reservaDeEvento = reservaService.createReservaForEvento(
                horario.getCanchaId(),
                cliente.getClienteId(),
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                savedEvento
            );
            reservasCreadas.add(reservaDeEvento);
        }
        
        savedEvento.setReservas(reservasCreadas);
        return toEventoResponse(savedEvento);
    }

    @Override
    @Transactional(readOnly = true)
    public EventoResponse getEventoById(Integer id) {
        return eventoRepository.findById(id)
                .map(this::toEventoResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoResponse> getAllEventos(Pageable pageable) {
        return eventoRepository.findAll(pageable).map(this::toEventoResponse);
    }

    @Override
    @Transactional
    public EventoResponse updateEvento(Integer id, UpdateEventoRequest request) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));

        if (evento.getEstado() == EstadoEvento.CANCELADO || evento.getEstado() == EstadoEvento.FINALIZADO) {
            throw new BusinessRuleException("No se puede modificar un evento que ya está finalizado o cancelado.");
        }

        if (request.getNombre() != null) evento.setNombre(request.getNombre());
        if (request.getDescripcion() != null) evento.setDescripcion(request.getDescripcion());
        if (request.getTipoEvento() != null) evento.setTipoEvento(request.getTipoEvento());
        if (request.getMontoPactado() != null) evento.setMontoPactado(request.getMontoPactado());
        if (request.getFechaInicio() != null) evento.setFechaInicio(request.getFechaInicio());
        if (request.getFechaFin() != null) evento.setFechaFin(request.getFechaFin());

        Evento updatedEvento = eventoRepository.save(evento);
        return toEventoResponse(updatedEvento);
    }

    @Override
    @Transactional
    public EventoResponse cancelarEvento(Integer id, CancelEventoRequest request) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));

        if (evento.getEstado() == EstadoEvento.CANCELADO || evento.getEstado() == EstadoEvento.FINALIZADO) {
            throw new BusinessRuleException("El evento ya está en un estado final.");
        }

        evento.setEstado(EstadoEvento.CANCELADO);

        for (Reserva reserva : evento.getReservas()) {
            reservaService.cancelReserva(reserva.getReservaId(), new CancelReservaRequest("Cancelado por cancelación del evento principal: " + evento.getNombre()));
        }

        Evento updatedEvento = eventoRepository.save(evento);
        return toEventoResponse(updatedEvento);
    }

    private EventoResponse toEventoResponse(Evento evento) {
        List<EventoResponse.ReservaInfo> reservaInfoList = evento.getReservas().stream()
                .map(reserva -> EventoResponse.ReservaInfo.builder()
                        .reservaId(reserva.getReservaId())
                        .canchaId(reserva.getCancha().getCanchaId())
                        .nombreCancha(reserva.getCancha().getNombre())
                        .fecha(reserva.getFecha())
                        .horario(reserva.getHoraInicio() + " - " + reserva.getHoraFin())
                        .build())
                .collect(Collectors.toList());

        return EventoResponse.builder()
                .id(evento.getEventoId())
                .sucursalId(evento.getSucursal().getSucursalId())
                .clienteId(evento.getCliente().getClienteId())
                .nombreCliente(evento.getCliente().getNombre())
                .nombre(evento.getNombre())
                .descripcion(evento.getDescripcion())
                .tipoEvento(evento.getTipoEvento())
                .montoPactado(evento.getMontoPactado())
                .fechaInicio(evento.getFechaInicio())
                .fechaFin(evento.getFechaFin())
                .estado(evento.getEstado())
                .reservasAsociadas(reservaInfoList)
                .build();
    }
}
