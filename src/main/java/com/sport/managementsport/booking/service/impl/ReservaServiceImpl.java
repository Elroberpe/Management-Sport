package com.sport.managementsport.booking.service.impl;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.booking.dto.*;
import com.sport.managementsport.booking.repository.ReservaRepository;
import com.sport.managementsport.booking.repository.ReservaSpecification;
import com.sport.managementsport.booking.service.ReservaService;
import com.sport.managementsport.common.enums.EstadoCancha;
import com.sport.managementsport.common.enums.EstadoReserva;
import com.sport.managementsport.common.enums.TipoReserva;
import com.sport.managementsport.common.enums.TipoTransaccion;
import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.company.service.CanchaService;
import com.sport.managementsport.events.domain.Mantenimiento;
import com.sport.managementsport.events.service.MantenimientoService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import com.sport.managementsport.finance.dto.CreatePagoRequest;
import com.sport.managementsport.finance.service.PagoService;
import com.sport.managementsport.identity.domain.Cliente;
import com.sport.managementsport.identity.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaServiceImpl.class);
    private final ReservaRepository reservaRepository;
    private final CanchaService canchaService;
    private final ClienteService clienteService;
    private final MantenimientoService mantenimientoService;
    private final PagoService pagoService;

    @Override
    @Transactional
    public ReservaResponse createReserva(CreateReservaRequest request) {
        LocalDateTime inicioReserva = request.getFecha().atTime(request.getHoraInicio());
        LocalDateTime finReserva = request.getFecha().atTime(request.getHoraFin());

        List<Mantenimiento> conflictingMantenimientos = mantenimientoService.findConflictingMantenimientos(request.getCanchaId(), inicioReserva, finReserva);
        if (!conflictingMantenimientos.isEmpty()) {
            throw new BusinessRuleException("El horario seleccionado no está disponible por un mantenimiento programado.");
        }

        List<Reserva> conflictingReservas = reservaRepository.findConflictingReservas(
                request.getCanchaId(), request.getFecha(), request.getHoraInicio(), request.getHoraFin());
        if (!conflictingReservas.isEmpty()) {
            throw new BusinessRuleException("El horario seleccionado ya no está disponible.");
        }

        Cancha cancha = canchaService.findCanchaEntityById(request.getCanchaId());
        Cliente cliente = clienteService.findClienteEntityById(request.getClienteId());
        
        if (cancha.getEstadoCancha() != EstadoCancha.DISPONIBLE) {
            throw new BusinessRuleException("No se puede reservar. La cancha no está disponible (estado: " + cancha.getEstadoCancha() + ").");
        }

        long minutes = Duration.between(request.getHoraInicio(), request.getHoraFin()).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes / 60.0);
        BigDecimal montoTotal = cancha.getPrecioHora().multiply(hours);

        Reserva reserva = new Reserva();
        reserva.setCancha(cancha);
        reserva.setCliente(cliente);
        reserva.setFecha(request.getFecha());
        reserva.setHoraInicio(request.getHoraInicio());
        reserva.setHoraFin(request.getHoraFin());
        reserva.setMontoTotal(montoTotal);
        reserva.setSaldoPendiente(montoTotal);
        reserva.setMontoPagado(BigDecimal.ZERO);
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        reserva.setTipoReserva(TipoReserva.REGULAR);

        Reserva savedReserva = reservaRepository.save(reserva);
        return toReservaResponse(savedReserva);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponse getReservaById(Integer id) {
        return reservaRepository.findById(id)
                .map(this::toReservaResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaResponse> getAllReservas(LocalDate fecha, Integer canchaId, Integer clienteId, EstadoReserva estado, Pageable pageable) {
        Specification<Reserva> spec = Specification.where(ReservaSpecification.fechaEquals(fecha))
                .and(ReservaSpecification.canchaIdEquals(canchaId))
                .and(ReservaSpecification.clienteIdEquals(clienteId))
                .and(ReservaSpecification.estadoEquals(estado));
        
        return reservaRepository.findAll(spec, pageable).map(this::toReservaResponse);
    }

    @Override
    @Transactional
    public ReservaResponse updateReserva(Integer id, UpdateReservaRequest request) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        if (request.getClienteId() != null) {
            Cliente nuevoCliente = clienteService.findClienteEntityById(request.getClienteId());
            reserva.setCliente(nuevoCliente);
        }

        Reserva updatedReserva = reservaRepository.save(reserva);
        return toReservaResponse(updatedReserva);
    }

    @Override
    @Transactional
    public ReservaResponse addPago(Integer reservaId, AddPagoToReservaRequest request) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + reservaId));

        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADO || reserva.getEstadoReserva() == EstadoReserva.REEMBOLSADO) {
            throw new BusinessRuleException("No se puede añadir un pago a una reserva cancelada o reembolsada.");
        }

        BigDecimal nuevoMontoPagado = reserva.getMontoPagado().add(request.getMonto());
        if (nuevoMontoPagado.compareTo(reserva.getMontoTotal()) > 0) {
            throw new BusinessRuleException("El monto del pago excede el saldo pendiente.");
        }

        pagoService.createPago(CreatePagoRequest.builder()
                .reserva(reserva)
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .tipoTransaccion(TipoTransaccion.INGRESO)
                .nota("Pago para reserva #" + reserva.getReservaId())
                .build());

        reserva.setMontoPagado(nuevoMontoPagado);
        reserva.setSaldoPendiente(reserva.getMontoTotal().subtract(nuevoMontoPagado));

        if (reserva.getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0) {
            reserva.setEstadoReserva(EstadoReserva.PAGADA);
        }

        Reserva updatedReserva = reservaRepository.save(reserva);
        return toReservaResponse(updatedReserva);
    }

    @Override
    @Transactional
    public ReservaResponse cancelReserva(Integer id, CancelReservaRequest request) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        if (reserva.getEstadoReserva() == EstadoReserva.COMPLETADO || reserva.getEstadoReserva() == EstadoReserva.CANCELADO || reserva.getEstadoReserva() == EstadoReserva.REEMBOLSADO) {
            throw new BusinessRuleException("La reserva ya está en un estado final y no puede ser cancelada.");
        }

        if (reserva.getMontoPagado().compareTo(BigDecimal.ZERO) > 0) {
            pagoService.createPago(CreatePagoRequest.builder()
                    .reserva(reserva)
                    .monto(reserva.getMontoPagado())
                    .metodoPago(reserva.getPagos().isEmpty() ? null : reserva.getPagos().get(0).getMetodoPago())
                    .tipoTransaccion(TipoTransaccion.SALIDA)
                    .nota("Reembolso por cancelación de reserva #" + id)
                    .build());
            
            reserva.setEstadoReserva(EstadoReserva.REEMBOLSADO);
        } else {
            reserva.setEstadoReserva(EstadoReserva.CANCELADO);
        }

        reserva.setMotivoCancelacion(request.getMotivo());
        
        Reserva updatedReserva = reservaRepository.save(reserva);
        return toReservaResponse(updatedReserva);
    }

    @Override
    @Transactional
    public ReservaResponse reprogramarReserva(Integer id, ReprogramarReservaRequest request) {
        Reserva reservaOriginal = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        if (reservaOriginal.getEstadoReserva() == EstadoReserva.COMPLETADO || reservaOriginal.getEstadoReserva() == EstadoReserva.CANCELADO) {
            throw new BusinessRuleException("No se puede reprogramar una reserva completada o cancelada.");
        }

        Cancha cancha = reservaOriginal.getCancha();
        List<Reserva> conflictingReservas = reservaRepository.findConflictingReservas(
                cancha.getCanchaId(), request.getNuevaFecha(), request.getNuevaHoraInicio(), request.getNuevaHoraFin());
        conflictingReservas.removeIf(r -> r.getReservaId().equals(id));
        if (!conflictingReservas.isEmpty()) {
            throw new BusinessRuleException("El nuevo horario no está disponible.");
        }
        
        reservaOriginal.setEstadoReserva(EstadoReserva.CANCELADO);
        reservaOriginal.setMotivoCancelacion("Reprogramada.");
        reservaRepository.save(reservaOriginal);

        CreateReservaRequest newRequest = new CreateReservaRequest(
            cancha.getCanchaId(),
            reservaOriginal.getCliente().getClienteId(),
            request.getNuevaFecha(),
            request.getNuevaHoraInicio(),
            request.getNuevaHoraFin()
        );
        
        return createReserva(newRequest);
    }

    @Override
    @Transactional
    public void deleteReserva(Integer id) {
        if (!reservaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reserva no encontrada con id: " + id);
        }

        if (!pagoService.getPagosByReservaId(id).isEmpty()) {
            throw new BusinessRuleException("No se puede eliminar una reserva que tiene pagos asociados. Por favor, cancélela en su lugar.");
        }

        reservaRepository.deleteById(id);
    }

    @Scheduled(cron = "0 5 * * * *")
    @Transactional
    public void actualizarEstadosDeReservas() {
        log.info("Iniciando tarea programada: Actualizando estados de reservas...");
        
        List<Reserva> reservasParaCompletar = reservaRepository.findReservasToUpdateStatus(
                EstadoReserva.PAGADA, LocalDate.now(), LocalTime.now());

        if (!reservasParaCompletar.isEmpty()) {
            log.info("Se encontraron {} reservas pagadas para marcar como COMPLETADAS.", reservasParaCompletar.size());
            for (Reserva reserva : reservasParaCompletar) {
                reserva.setEstadoReserva(EstadoReserva.COMPLETADO);
                reservaRepository.save(reserva);
            }
        }
        
        List<Reserva> reservasParaCancelar = reservaRepository.findReservasToUpdateStatus(
                EstadoReserva.PENDIENTE, LocalDate.now(), LocalTime.now());
        
        if (!reservasParaCancelar.isEmpty()) {
            log.info("Se encontraron {} reservas pendientes expiradas para CANCELAR.", reservasParaCancelar.size());
            for (Reserva reserva : reservasParaCancelar) {
                reserva.setEstadoReserva(EstadoReserva.CANCELADO);
                reserva.setMotivoCancelacion("Cancelada automáticamente por expiración de tiempo sin pago.");
                reservaRepository.save(reserva);
            }
        }
        
        log.info("Tarea de actualización de estados de reservas finalizada.");
    }

    private ReservaResponse toReservaResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getReservaId(),
                reserva.getCancha().getCanchaId(),
                reserva.getCancha().getNombre(),
                reserva.getCliente().getClienteId(),
                reserva.getCliente().getNombre(),
                reserva.getUsuario() != null ? reserva.getUsuario().getUsuarioId() : null,
                reserva.getUsuario() != null ? reserva.getUsuario().getNombre() : null,
                reserva.getEvento() != null ? reserva.getEvento().getEventoId() : null,
                reserva.getTipoReserva(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                reserva.getMontoTotal(),
                reserva.getMontoPagado(),
                reserva.getSaldoPendiente(),
                reserva.getEstadoReserva()
        );
    }
}
