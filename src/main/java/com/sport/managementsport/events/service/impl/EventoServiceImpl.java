package com.sport.managementsport.events.service.impl;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.booking.dto.CancelReservaRequest;
import com.sport.managementsport.booking.service.ReservaService;
import com.sport.managementsport.common.enums.EstadoEvento;
import com.sport.managementsport.common.enums.TipoTransaccion;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.events.domain.Evento;
import com.sport.managementsport.events.domain.EventoHorario;
import com.sport.managementsport.events.dto.*;
import com.sport.managementsport.events.repository.EventoHorarioRepository;
import com.sport.managementsport.events.repository.EventoRepository;
import com.sport.managementsport.events.service.EventoService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import com.sport.managementsport.finance.dto.CreatePagoRequest;
import com.sport.managementsport.finance.service.PagoService;
import com.sport.managementsport.identity.domain.Cliente;
import com.sport.managementsport.identity.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final EventoHorarioRepository eventoHorarioRepository;
    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final SucursalService sucursalService;
    private final PagoService pagoService;

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
        evento.setSaldoPendiente(request.getMontoPactado());
        evento.setFechaInicio(request.getFechaInicio());
        evento.setFechaFin(request.getFechaFin());
        
        Evento savedEvento = eventoRepository.save(evento);

        List<Reserva> reservasCreadas = new ArrayList<>();
        for (CreateEventoRequest.HorarioBloqueoDto horarioDto : request.getHorarios()) {
            EventoHorario eventoHorario = new EventoHorario();
            eventoHorario.setEvento(savedEvento);
            eventoHorario.setFecha(horarioDto.getFecha());
            eventoHorario.setHoraInicio(horarioDto.getHoraInicio());
            eventoHorario.setHoraFin(horarioDto.getHoraFin());
            evento.getHorarios().add(eventoHorario);

            Reserva reservaDeEvento = reservaService.createReservaForEvento(
                horarioDto.getCanchaId(),
                cliente.getClienteId(),
                horarioDto.getFecha(),
                horarioDto.getHoraInicio(),
                horarioDto.getHoraFin(),
                savedEvento
            );
            reservasCreadas.add(reservaDeEvento);
        }
        
        savedEvento.setReservas(reservasCreadas);
        return toEventoResponse(eventoRepository.save(savedEvento));
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
        if (request.getMontoPactado() != null) {
            evento.setMontoPactado(request.getMontoPactado());
            evento.setSaldoPendiente(request.getMontoPactado().subtract(evento.getMontoPagado()));
        }
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

    @Override
    @Transactional
    public EventoResponse addPago(Integer id, AddPagoToEventoRequest request) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));

        if (evento.getEstado() == EstadoEvento.CANCELADO || evento.getEstado() == EstadoEvento.FINALIZADO) {
            throw new BusinessRuleException("No se puede añadir un pago a un evento finalizado o cancelado.");
        }

        BigDecimal nuevoMontoPagado = evento.getMontoPagado().add(request.getMonto());
        if (nuevoMontoPagado.compareTo(evento.getMontoPactado()) > 0) {
            throw new BusinessRuleException("El monto del pago excede el saldo pendiente del evento.");
        }

        pagoService.createPago(CreatePagoRequest.builder()
                .evento(evento)
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .tipoTransaccion(TipoTransaccion.INGRESO)
                .nota("Pago para evento #" + evento.getEventoId())
                .build());

        evento.setMontoPagado(nuevoMontoPagado);
        evento.setSaldoPendiente(evento.getMontoPactado().subtract(nuevoMontoPagado));
        
        Evento updatedEvento = eventoRepository.save(evento);
        return toEventoResponse(updatedEvento);
    }

    @Override
    @Transactional
    public EventoResponse reprogramarEvento(Integer id, ReprogramarEventoRequest request) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));

        if (evento.getEstado() == EstadoEvento.CANCELADO || evento.getEstado() == EstadoEvento.FINALIZADO) {
            throw new BusinessRuleException("No se puede reprogramar un evento finalizado o cancelado.");
        }
        if (evento.getReservas().size() != request.getNuevosHorarios().size()) {
            throw new BusinessRuleException("El número de nuevos horarios no coincide con el número de reservas del evento.");
        }

        for (var horario : request.getNuevosHorarios()) {
            reservaService.validateHorarioDisponible(horario.getCanchaId(), horario.getFecha(), horario.getHoraInicio(), horario.getHoraFin(), id);
        }

        eventoHorarioRepository.deleteByEventoEventoId(id);
        evento.getHorarios().clear();

        for (int i = 0; i < request.getNuevosHorarios().size(); i++) {
            var nuevoHorarioDto = request.getNuevosHorarios().get(i);
            Reserva reservaAActualizar = evento.getReservas().get(i);

            reservaAActualizar.setFecha(nuevoHorarioDto.getFecha());
            reservaAActualizar.setHoraInicio(nuevoHorarioDto.getHoraInicio());
            reservaAActualizar.setHoraFin(nuevoHorarioDto.getHoraFin());

            EventoHorario nuevoHorario = new EventoHorario();
            nuevoHorario.setEvento(evento);
            nuevoHorario.setFecha(nuevoHorarioDto.getFecha());
            nuevoHorario.setHoraInicio(nuevoHorarioDto.getHoraInicio());
            nuevoHorario.setHoraFin(nuevoHorarioDto.getHoraFin());
            evento.getHorarios().add(nuevoHorario);
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
                .montoPagado(evento.getMontoPagado())
                .saldoPendiente(evento.getSaldoPendiente())
                .fechaInicio(evento.getFechaInicio())
                .fechaFin(evento.getFechaFin())
                .estado(evento.getEstado())
                .reservasAsociadas(reservaInfoList)
                .build();
    }
}
