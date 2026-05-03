package com.sport.managementsport.events.service.impl;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.booking.service.ReservaService;
import com.sport.managementsport.common.enums.EstadoEvento;
import com.sport.managementsport.common.enums.EstadoReserva;
import com.sport.managementsport.common.enums.RolUsuario;
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
import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
            LocalDateTime startDateTime = horario.getFecha().atTime(horario.getHoraInicio());
            LocalDateTime endDateTime = horario.getFecha().atTime(horario.getHoraFin());
            reservaService.validateHorarioDisponible(horario.getCanchaId(), startDateTime, endDateTime);
        }

        Evento evento = new Evento();
        evento.setSucursal(sucursal);
        evento.setCliente(cliente);
        evento.setNombre(request.getNombre());
        evento.setDescripcion(request.getDescripcion());
        evento.setTipoEvento(request.getTipoEvento());
        evento.setMontoPactado(request.getMontoPactado());
        evento.setMontoPagado(BigDecimal.ZERO);
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

            LocalDateTime startDateTime = horarioDto.getFecha().atTime(horarioDto.getHoraInicio());
            LocalDateTime endDateTime = horarioDto.getFecha().atTime(horarioDto.getHoraFin());
            Reserva reservaDeEvento = reservaService.createReservaForEvento(
                horarioDto.getCanchaId(),
                cliente.getClienteId(),
                startDateTime,
                endDateTime,
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
    public Page<EventoResponse> getAllEventos(Integer sucursalIdParam, Pageable pageable) {
        Usuario currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser.getRol() == RolUsuario.SUPERADMIN) {
            if (sucursalIdParam != null) {
                return eventoRepository.findBySucursalSucursalId(sucursalIdParam, pageable).map(this::toEventoResponse);
            }
            return eventoRepository.findAll(pageable).map(this::toEventoResponse);
        } else {
            Integer sucursalId = currentUser.getSucursal().getSucursalId();
            return eventoRepository.findBySucursalSucursalId(sucursalId, pageable).map(this::toEventoResponse);
        }
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

        SimulacionCancelacionResponse simulacion = calcularSimulacion(evento);

        if (request.getMontoReembolso().compareTo(simulacion.getReembolsoMaximoPermitido()) > 0) {
            throw new BusinessRuleException("El monto a reembolsar excede el máximo permitido. " + simulacion.getMensaje() + " Máximo permitido: S/ " + simulacion.getReembolsoMaximoPermitido());
        }

        if (request.getMontoReembolso().compareTo(BigDecimal.ZERO) > 0) {
            pagoService.createPago(CreatePagoRequest.builder()
                    .evento(evento)
                    .monto(request.getMontoReembolso())
                    .tipoTransaccion(TipoTransaccion.SALIDA)
                    .nota(request.getNotaReembolso() != null ? request.getNotaReembolso() : "Reembolso por cancelación de evento.")
                    .metodoPago(evento.getPagos().stream().findFirst().map(p -> p.getMetodoPago()).orElse(null))
                    .build());
        }

        evento.setMontoPagado(evento.getMontoPagado().subtract(request.getMontoReembolso()));
        evento.setSaldoPendiente(evento.getSaldoPendiente().add(request.getMontoReembolso()));

        for (Reserva reserva : evento.getReservas()) {
            reserva.setEstadoReserva(EstadoReserva.CANCELADO);
            reserva.setMotivoCancelacion("Evento principal cancelado: " + evento.getNombre());
        }

        evento.setEstado(EstadoEvento.CANCELADO);
        
        Evento updatedEvento = eventoRepository.save(evento);
        return toEventoResponse(updatedEvento);
    }

    @Override
    @Transactional(readOnly = true)
    public SimulacionCancelacionResponse simularCancelacion(Integer id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));

        if (evento.getEstado() == EstadoEvento.CANCELADO || evento.getEstado() == EstadoEvento.FINALIZADO) {
            throw new BusinessRuleException("El evento ya está en un estado final y no se puede simular su cancelación.");
        }

        return calcularSimulacion(evento);
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
            LocalDateTime startDateTime = horario.getFecha().atTime(horario.getHoraInicio());
            LocalDateTime endDateTime = horario.getFecha().atTime(horario.getHoraFin());
            reservaService.validateHorarioDisponible(horario.getCanchaId(), startDateTime, endDateTime, id);
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

    @Override
    @Transactional
    public void revertirSaldosPorAnulacion(Integer eventoId, BigDecimal montoAnulado) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + eventoId));

        evento.setMontoPagado(evento.getMontoPagado().subtract(montoAnulado));
        evento.setSaldoPendiente(evento.getSaldoPendiente().add(montoAnulado));
        
        eventoRepository.save(evento);
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void actualizarEstadosEventos() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Evento> eventosProgramados = eventoRepository.findByEstado(EstadoEvento.PROGRAMADO);

        for (Evento evento : eventosProgramados) {
            LocalDateTime earliestStart = getEarliestStart(evento);
            if (earliestStart == null) continue;

            // Lógica de Auto-Cancelación por falta de pago
            LocalDateTime deadlinePago = earliestStart.minusHours(3);
            BigDecimal montoMinimo = evento.getMontoPactado().multiply(new BigDecimal("0.50"));
            if (ahora.isAfter(deadlinePago) && evento.getMontoPagado().compareTo(montoMinimo) < 0) {
                log.info("Cancelando evento #{} por no cumplir con el pago mínimo.", evento.getEventoId());
                cancelarEventoPorSistema(evento, "Cancelado automáticamente por no cumplir con el pago mínimo del 50%.");
                continue; // Pasar al siguiente evento
            }

            // Lógica para pasar a EN_CURSO
            if (!ahora.isBefore(earliestStart)) {
                evento.setEstado(EstadoEvento.EN_CURSO);
                eventoRepository.save(evento);
            }
        }

        // Lógica para pasar a FINALIZADO
        List<Evento> eventosEnCurso = eventoRepository.findByEstado(EstadoEvento.EN_CURSO);
        for (Evento evento : eventosEnCurso) {
            LocalDateTime latestEnd = getLatestEnd(evento);
            if (latestEnd != null && ahora.isAfter(latestEnd)) {
                evento.setEstado(EstadoEvento.FINALIZADO);
                eventoRepository.save(evento);
            }
        }
    }

    private SimulacionCancelacionResponse calcularSimulacion(Evento evento) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime earliestStart = getEarliestStart(evento);
        
        boolean aplicaPenalidad = false;
        BigDecimal penalidad = BigDecimal.ZERO;
        String mensaje = "Cancelación con más de 4 horas de anticipación. Reembolso completo permitido.";

        if (earliestStart != null && ahora.isAfter(earliestStart.minusHours(4))) {
            aplicaPenalidad = true;
            penalidad = evento.getMontoPactado().multiply(new BigDecimal("0.30"));
            mensaje = "Cancelación a menos de 4 horas del evento. Se retiene el 30% del costo total (S/ " + penalidad + ") por penalidad.";
        }

        BigDecimal reembolsoMaximo = evento.getMontoPagado().subtract(penalidad);
        if (reembolsoMaximo.compareTo(BigDecimal.ZERO) < 0) {
            reembolsoMaximo = BigDecimal.ZERO;
        }

        return SimulacionCancelacionResponse.builder()
                .montoPagado(evento.getMontoPagado())
                .penalidadAplicable(penalidad)
                .reembolsoMaximoPermitido(reembolsoMaximo)
                .aplicaPenalidad(aplicaPenalidad)
                .mensaje(mensaje)
                .build();
    }
    
    private void cancelarEventoPorSistema(Evento evento, String motivo) {
        evento.setEstado(EstadoEvento.CANCELADO);
        for (Reserva reserva : evento.getReservas()) {
            reserva.setEstadoReserva(EstadoReserva.CANCELADO);
            reserva.setMotivoCancelacion(motivo);
        }
        eventoRepository.save(evento);
    }

    private LocalDateTime getEarliestStart(Evento evento) {
        return evento.getHorarios().stream()
                .map(h -> h.getFecha().atTime(h.getHoraInicio()))
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime getLatestEnd(Evento evento) {
        return evento.getHorarios().stream()
                .map(h -> h.getFecha().atTime(h.getHoraFin()))
                .max(LocalDateTime::compareTo)
                .orElse(null);
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
