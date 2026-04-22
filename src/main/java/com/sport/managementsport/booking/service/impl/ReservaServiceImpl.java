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
import com.sport.managementsport.events.domain.Evento;
import com.sport.managementsport.events.service.MantenimientoService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import com.sport.managementsport.finance.dto.CreatePagoRequest;
import com.sport.managementsport.finance.service.PagoService;
import com.sport.managementsport.identity.domain.Cliente;
import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.service.ClienteService;
import com.sport.managementsport.identity.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
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
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaServiceImpl.class);
    private static final long HORAS_LIMITE_REEMBOLSO_COMPLETO = 3;
    private static final BigDecimal PORCENTAJE_PENALIDAD = new BigDecimal("0.30");

    private final ReservaRepository reservaRepository;
    private final CanchaService canchaService;
    private final ClienteService clienteService;
    private final MantenimientoService mantenimientoService;
    private final PagoService pagoService;
    private final UsuarioService usuarioService;

    public ReservaServiceImpl(ReservaRepository reservaRepository, CanchaService canchaService, ClienteService clienteService, @Lazy MantenimientoService mantenimientoService, @Lazy PagoService pagoService, UsuarioService usuarioService) {
        this.reservaRepository = reservaRepository;
        this.canchaService = canchaService;
        this.clienteService = clienteService;
        this.mantenimientoService = mantenimientoService;
        this.pagoService = pagoService;
        this.usuarioService = usuarioService;
    }

    @Override
    @Transactional
    public ReservaResponse createReserva(CreateReservaRequest request) {
        LocalDateTime startDateTime = request.getFecha().atTime(request.getHoraInicio());
        LocalDateTime endDateTime = request.getFecha().atTime(request.getHoraFin());

        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("No se puede crear una reserva para una fecha u hora pasada.");
        }

        this.validateHorarioDisponible(request.getCanchaId(), startDateTime, endDateTime);

        Cancha cancha = canchaService.findCanchaEntityById(request.getCanchaId());
        Cliente cliente = clienteService.findClienteEntityById(request.getClienteId());
        Usuario usuarioOperador = usuarioService.findUsuarioEntityById(1);

        if (cancha.getEstadoCancha() != EstadoCancha.DISPONIBLE) {
            throw new BusinessRuleException("No se puede reservar. La cancha no está disponible (estado: " + cancha.getEstadoCancha() + ").");
        }

        long minutes = Duration.between(request.getHoraInicio(), request.getHoraFin()).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes / 60.0);
        BigDecimal montoTotal = cancha.getPrecioHora().multiply(hours);

        Reserva reserva = new Reserva();
        reserva.setCancha(cancha);
        reserva.setCliente(cliente);
        reserva.setUsuario(usuarioOperador);
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
    public Page<ReservaResponse> getAllReservas(LocalDate fechaDesde, LocalDate fechaHasta, Integer canchaId, Integer clienteId, Integer sucursalId, EstadoReserva estado, Pageable pageable) {
        Specification<Reserva> spec = Specification.where(ReservaSpecification.fechaBetween(fechaDesde, fechaHasta))
                .and(ReservaSpecification.canchaIdEquals(canchaId))
                .and(ReservaSpecification.clienteIdEquals(clienteId))
                .and(ReservaSpecification.sucursalIdEquals(sucursalId))
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

        if (reserva.getMontoPagado().compareTo(BigDecimal.ZERO) <= 0) {
            reserva.setEstadoReserva(EstadoReserva.CANCELADO);
            reserva.setMotivoCancelacion(request.getMotivo());
            Reserva updatedReserva = reservaRepository.save(reserva);
            return toReservaResponse(updatedReserva);
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioReserva = reserva.getFecha().atTime(reserva.getHoraInicio());
        long horasRestantes = Duration.between(ahora, inicioReserva).toHours();

        BigDecimal montoAReembolsar;

        if (horasRestantes >= HORAS_LIMITE_REEMBOLSO_COMPLETO) {
            montoAReembolsar = reserva.getMontoPagado();
        } else {
            BigDecimal montoPenalidad = reserva.getMontoTotal().multiply(PORCENTAJE_PENALIDAD);
            BigDecimal montoCubiertoPorCliente = reserva.getMontoPagado();
            
            montoAReembolsar = montoCubiertoPorCliente.subtract(montoPenalidad);
            if (montoAReembolsar.compareTo(BigDecimal.ZERO) < 0) {
                montoAReembolsar = BigDecimal.ZERO;
            }
        }

        if (montoAReembolsar.compareTo(BigDecimal.ZERO) > 0) {
            pagoService.createPago(CreatePagoRequest.builder()
                    .reserva(reserva)
                    .monto(montoAReembolsar)
                    .metodoPago(reserva.getPagos().isEmpty() ? null : reserva.getPagos().get(0).getMetodoPago())
                    .tipoTransaccion(TipoTransaccion.SALIDA)
                    .nota("Reembolso por cancelación con política aplicada. Horas restantes: " + horasRestantes)
                    .build());
        }

        reserva.setEstadoReserva(EstadoReserva.REEMBOLSADO);
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
        LocalDateTime newStartDateTime = request.getNuevaFecha().atTime(request.getNuevaHoraInicio());
        LocalDateTime newEndDateTime = request.getNuevaFecha().atTime(request.getNuevaHoraFin());
        validateHorarioDisponibleIgnoringSelf(cancha.getCanchaId(), newStartDateTime, newEndDateTime, id);

        long newMinutes = Duration.between(request.getNuevaHoraInicio(), request.getNuevaHoraFin()).toMinutes();
        BigDecimal newHours = BigDecimal.valueOf(newMinutes / 60.0);
        BigDecimal nuevoMontoTotal = cancha.getPrecioHora().multiply(newHours);

        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setCancha(cancha);
        nuevaReserva.setCliente(reservaOriginal.getCliente());
        nuevaReserva.setUsuario(reservaOriginal.getUsuario());
        nuevaReserva.setFecha(request.getNuevaFecha());
        nuevaReserva.setHoraInicio(request.getNuevaHoraInicio());
        nuevaReserva.setHoraFin(request.getNuevaHoraFin());
        nuevaReserva.setTipoReserva(TipoReserva.REGULAR);
        nuevaReserva.setMontoTotal(nuevoMontoTotal);
        nuevaReserva.setMontoPagado(reservaOriginal.getMontoPagado());
        nuevaReserva.setSaldoPendiente(nuevoMontoTotal.subtract(reservaOriginal.getMontoPagado()));

        if (nuevaReserva.getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0) {
            nuevaReserva.setEstadoReserva(EstadoReserva.PAGADA);
        } else {
            nuevaReserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        }
        
        Reserva savedNuevaReserva = reservaRepository.save(nuevaReserva);
        pagoService.reasignarPagos(reservaOriginal, savedNuevaReserva);

        reservaOriginal.setEstadoReserva(EstadoReserva.CANCELADO);
        reservaOriginal.setMotivoCancelacion("Reprogramada a reserva ID: " + savedNuevaReserva.getReservaId());
        reservaOriginal.setMontoPagado(BigDecimal.ZERO);
        reservaOriginal.setSaldoPendiente(reservaOriginal.getMontoTotal());
        reservaRepository.save(reservaOriginal);

        return toReservaResponse(savedNuevaReserva);
    }

    @Override
    @Transactional
    public ReservaResponse registrarReembolsoManual(Integer reservaId, CreateReembolsoRequest request) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + reservaId));

        if (reserva.getSaldoPendiente().compareTo(BigDecimal.ZERO) >= 0) {
            throw new BusinessRuleException("Esta reserva no tiene un crédito a favor para reembolsar.");
        }

        BigDecimal creditoDisponible = reserva.getSaldoPendiente().abs();

        if (request.getMonto().compareTo(creditoDisponible) > 0) {
            throw new BusinessRuleException("El monto a reembolsar (" + request.getMonto() +
                                            ") no puede ser mayor que el crédito disponible (" + creditoDisponible + ").");
        }

        pagoService.createPago(CreatePagoRequest.builder()
                .reserva(reserva)
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .tipoTransaccion(TipoTransaccion.SALIDA)
                .nota(request.getNota() != null ? request.getNota() : "Reembolso manual de crédito.")
                .build());

        reserva.setMontoPagado(reserva.getMontoPagado().subtract(request.getMonto()));
        reserva.setSaldoPendiente(reserva.getSaldoPendiente().add(request.getMonto()));

        Reserva reservaActualizada = reservaRepository.save(reserva);
        return toReservaResponse(reservaActualizada);
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
        LocalDateTime now = LocalDateTime.now();
        
        List<Reserva> reservasParaCompletar = reservaRepository.findReservasToUpdateStatus(EstadoReserva.PAGADA.name(), now);
        if (!reservasParaCompletar.isEmpty()) {
            log.info("Se encontraron {} reservas pagadas para marcar como COMPLETADAS.", reservasParaCompletar.size());
            for (Reserva reserva : reservasParaCompletar) {
                reserva.setEstadoReserva(EstadoReserva.COMPLETADO);
                reservaRepository.save(reserva);
            }
        }
        
        List<Reserva> reservasParaCancelar = reservaRepository.findReservasToUpdateStatus(EstadoReserva.PENDIENTE.name(), now);
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

    @Override
    public List<Reserva> findConflictingReservas(Integer canchaId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return reservaRepository.findConflictingReservas(canchaId, startDateTime, endDateTime);
    }

    @Override
    public void validateHorarioDisponible(Integer canchaId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (!mantenimientoService.findConflictingMantenimientos(canchaId, startDateTime, endDateTime).isEmpty()) {
            throw new BusinessRuleException("El horario se solapa con un mantenimiento.");
        }
        if (!findConflictingReservas(canchaId, startDateTime, endDateTime).isEmpty()) {
            throw new BusinessRuleException("El horario se solapa con una reserva existente.");
        }
    }
    
    @Override
    public void validateHorarioDisponibleIgnoringSelf(Integer canchaId, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer reservaIdToIgnore) {
        if (!mantenimientoService.findConflictingMantenimientos(canchaId, startDateTime, endDateTime).isEmpty()) {
            throw new BusinessRuleException("El horario se solapa con un mantenimiento.");
        }
        if (!reservaRepository.findConflictingReservasIgnoringSelf(canchaId, startDateTime, endDateTime, reservaIdToIgnore).isEmpty()) {
            throw new BusinessRuleException("El horario se solapa con una reserva existente.");
        }
    }

    @Override
    public void validateHorarioDisponible(Integer canchaId, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer eventoIdToIgnore) {
        if (!mantenimientoService.findConflictingMantenimientos(canchaId, startDateTime, endDateTime).isEmpty()) {
            throw new BusinessRuleException("El horario se solapa con un mantenimiento.");
        }
        if (!reservaRepository.findConflictingReservasIgnoringEvent(canchaId, startDateTime, endDateTime, eventoIdToIgnore).isEmpty()) {
            throw new BusinessRuleException("El horario se solapa con una reserva existente.");
        }
    }

    @Override
    @Transactional
    public Reserva createReservaForEvento(Integer canchaId, Integer clienteId, LocalDateTime startDateTime, LocalDateTime endDateTime, Evento evento) {
        Cancha cancha = canchaService.findCanchaEntityById(canchaId);
        Cliente cliente = clienteService.findClienteEntityById(clienteId);
        Usuario usuario = usuarioService.findUsuarioEntityById(1);

        Reserva reserva = new Reserva();
        reserva.setCancha(cancha);
        reserva.setCliente(cliente);
        reserva.setUsuario(usuario);
        reserva.setEvento(evento);
        reserva.setTipoReserva(TipoReserva.EVENTO);
        reserva.setFecha(startDateTime.toLocalDate());
        reserva.setHoraInicio(startDateTime.toLocalTime());
        reserva.setHoraFin(endDateTime.toLocalTime());
        reserva.setMontoTotal(BigDecimal.ZERO);
        reserva.setSaldoPendiente(BigDecimal.ZERO);
        reserva.setMontoPagado(BigDecimal.ZERO);
        reserva.setEstadoReserva(EstadoReserva.PAGADA);

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public void revertirSaldosPorAnulacion(Integer reservaId, BigDecimal montoAnulado) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + reservaId));

        reserva.setMontoPagado(reserva.getMontoPagado().subtract(montoAnulado));
        reserva.setSaldoPendiente(reserva.getSaldoPendiente().add(montoAnulado));

        if (reserva.getEstadoReserva() == EstadoReserva.PAGADA) {
            reserva.setEstadoReserva(EstadoReserva.PENDIENTE);
        }
        
        reservaRepository.save(reserva);
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
