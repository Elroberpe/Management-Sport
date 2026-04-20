package com.sport.managementsport.booking.service;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.booking.dto.*;
import com.sport.managementsport.common.enums.EstadoReserva;
import com.sport.managementsport.events.domain.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReservaService {

    // --- Métodos para la API pública (usados por ReservaController) ---
    ReservaResponse createReserva(CreateReservaRequest request);
    ReservaResponse getReservaById(Integer id);
    Page<ReservaResponse> getAllReservas(LocalDate fecha, Integer canchaId, Integer clienteId, EstadoReserva estado, Pageable pageable);
    ReservaResponse updateReserva(Integer id, UpdateReservaRequest request);
    ReservaResponse addPago(Integer reservaId, AddPagoToReservaRequest request);
    ReservaResponse cancelReserva(Integer id, CancelReservaRequest request);
    ReservaResponse reprogramarReserva(Integer id, ReprogramarReservaRequest request);
    void deleteReserva(Integer id);

    // --- Métodos para uso interno entre servicios ---
    void validateHorarioDisponible(Integer canchaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
    void validateHorarioDisponible(Integer canchaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Integer eventoIdToIgnore);
    Reserva createReservaForEvento(Integer canchaId, Integer clienteId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Evento evento);
}
