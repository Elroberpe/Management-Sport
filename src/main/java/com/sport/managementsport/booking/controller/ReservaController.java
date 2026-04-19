package com.sport.managementsport.booking.controller;

import com.sport.managementsport.booking.dto.*;
import com.sport.managementsport.booking.service.ReservaService;
import com.sport.managementsport.common.enums.EstadoReserva;
import com.sport.managementsport.finance.dto.PagoResponse;
import com.sport.managementsport.finance.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<ReservaResponse> createReserva(@Valid @RequestBody CreateReservaRequest request) {
        ReservaResponse newReserva = reservaService.createReserva(request);
        return new ResponseEntity<>(newReserva, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> getReservaById(@PathVariable Integer id) {
        ReservaResponse reserva = reservaService.getReservaById(id);
        return ResponseEntity.ok(reserva);
    }

    @GetMapping
    public ResponseEntity<Page<ReservaResponse>> getAllReservas(
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) Integer canchaId,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) EstadoReserva estado,
            @PageableDefault(size = 20, sort = "fecha") Pageable pageable) {
        Page<ReservaResponse> reservas = reservaService.getAllReservas(fecha, canchaId, clienteId, estado, pageable);
        return ResponseEntity.ok(reservas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> updateReserva(@PathVariable Integer id, @Valid @RequestBody UpdateReservaRequest request) {
        ReservaResponse updatedReserva = reservaService.updateReserva(id, request);
        return ResponseEntity.ok(updatedReserva);
    }

    @PostMapping("/{id}/pagos")
    public ResponseEntity<ReservaResponse> addPago(@PathVariable Integer id, @Valid @RequestBody AddPagoToReservaRequest request) {
        ReservaResponse updatedReserva = reservaService.addPago(id, request);
        return ResponseEntity.ok(updatedReserva);
    }

    @GetMapping("/{id}/pagos")
    public ResponseEntity<List<PagoResponse>> getHistorialPagos(@PathVariable Integer id) {
        List<PagoResponse> pagos = pagoService.getPagosByReservaId(id);
        return ResponseEntity.ok(pagos);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelReserva(@PathVariable Integer id, @Valid @RequestBody CancelReservaRequest request) {
        ReservaResponse updatedReserva = reservaService.cancelReserva(id, request);
        return ResponseEntity.ok(updatedReserva);
    }

    @PostMapping("/{id}/reprogramar")
    public ResponseEntity<ReservaResponse> reprogramarReserva(@PathVariable Integer id, @Valid @RequestBody ReprogramarReservaRequest request) {
        ReservaResponse nuevaReserva = reservaService.reprogramarReserva(id, request);
        return ResponseEntity.ok(nuevaReserva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Integer id) {
        reservaService.deleteReserva(id);
        return ResponseEntity.noContent().build();
    }
}
