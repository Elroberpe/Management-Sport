package com.sport.managementsport.booking.service;

import com.sport.managementsport.booking.dto.*;
import com.sport.managementsport.common.enums.EstadoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReservaService {

    ReservaResponse createReserva(CreateReservaRequest request);
    ReservaResponse getReservaById(Integer id);
    Page<ReservaResponse> getAllReservas(LocalDate fecha, Integer canchaId, Integer clienteId, EstadoReserva estado, Pageable pageable);
    ReservaResponse updateReserva(Integer id, UpdateReservaRequest request);
    ReservaResponse addPago(Integer reservaId, AddPagoToReservaRequest request);
    ReservaResponse cancelReserva(Integer id, CancelReservaRequest request);
    ReservaResponse reprogramarReserva(Integer id, ReprogramarReservaRequest request);
    void deleteReserva(Integer id);
}
