package com.sport.managementsport.booking.service;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.booking.dto.*;
import com.sport.managementsport.common.enums.EstadoReserva;
import com.sport.managementsport.dashboard.dto.KpiResponse;
import com.sport.managementsport.events.domain.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ReservaService {

    // --- Métodos para la API pública (usados por ReservaController) ---
    ReservaResponse createReserva(CreateReservaRequest request);
    ReservaResponse getReservaById(Integer id);
    Page<ReservaResponse> getAllReservas(LocalDate fechaDesde, LocalDate fechaHasta, Integer canchaId, Integer clienteId, Integer sucursalId, EstadoReserva estado, Pageable pageable);
    ReservaResponse updateReserva(Integer id, UpdateReservaRequest request);
    ReservaResponse addPago(Integer reservaId, AddPagoToReservaRequest request);
    ReservaResponse cancelReserva(Integer id, CancelReservaRequest request);
    ReservaResponse reprogramarReserva(Integer id, ReprogramarReservaRequest request);
    ReservaResponse registrarReembolsoManual(Integer reservaId, CreateReembolsoRequest request);
    void deleteReserva(Integer id);
    KpiResponse getReservasCompletadasHoy(Integer sucursalId);

    // --- Métodos para uso interno entre servicios ---
    List<Reserva> findConflictingReservas(Integer canchaId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    void validateHorarioDisponible(Integer canchaId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    void validateHorarioDisponible(Integer canchaId, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer eventoIdToIgnore);
    void validateHorarioDisponibleIgnoringSelf(Integer canchaId, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer reservaIdToIgnore);
    Reserva createReservaForEvento(Integer canchaId, Integer clienteId, LocalDateTime startDateTime, LocalDateTime endDateTime, Evento evento);
    void revertirSaldosPorAnulacion(Integer reservaId, BigDecimal montoAnulado);
}
