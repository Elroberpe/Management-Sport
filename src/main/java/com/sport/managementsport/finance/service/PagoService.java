package com.sport.managementsport.finance.service;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.common.enums.MetodoPago;
import com.sport.managementsport.finance.dto.AnularPagoRequest;
import com.sport.managementsport.finance.dto.CreatePagoRequest;
import com.sport.managementsport.finance.dto.PagoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PagoService {
    PagoResponse createPago(CreatePagoRequest request);
    List<PagoResponse> getPagosByReservaId(Integer reservaId);
    PagoResponse getPagoById(Integer id);
    Page<PagoResponse> getAllPagos(LocalDate desde, LocalDate hasta, MetodoPago metodo, Pageable pageable);
    void anularPago(Integer id, AnularPagoRequest request);

    // Nuevo método para la reprogramación
    void reasignarPagos(Reserva reservaOriginal, Reserva nuevaReserva);
}
