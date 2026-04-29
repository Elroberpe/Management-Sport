package com.sport.managementsport.booking.controller;

import com.sport.managementsport.booking.dto.*;
import com.sport.managementsport.booking.service.ReservaService;
import com.sport.managementsport.common.enums.EstadoReserva;
import com.sport.managementsport.dashboard.dto.KpiResponse;
import com.sport.managementsport.finance.dto.PagoResponse;
import com.sport.managementsport.finance.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) Integer canchaId,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) Integer sucursalId,
            @RequestParam(name = "estadoReserva", required = false) EstadoReserva estado,
            @PageableDefault(size = 20, sort = "fecha") Pageable pageable) {

        Sort sort = pageable.getSort();
        List<Sort.Order> translatedOrders = sort.stream()
                .map(order -> {
                    if (order.getProperty().equalsIgnoreCase("id")) {
                        return new Sort.Order(order.getDirection(), "reservaId");
                    }
                    return order;
                })
                .collect(Collectors.toList());
        
        Pageable translatedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(translatedOrders));

        Page<ReservaResponse> reservas = reservaService.getAllReservas(fechaDesde, fechaHasta, canchaId, clienteId, sucursalId, estado, translatedPageable);
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

    @PostMapping("/{id}/reembolsos")
    public ResponseEntity<ReservaResponse> registrarReembolsoManual(
            @PathVariable Integer id,
            @Valid @RequestBody CreateReembolsoRequest request) {
        ReservaResponse reservaActualizada = reservaService.registrarReembolsoManual(id, request);
        return ResponseEntity.ok(reservaActualizada);
    }

    @GetMapping("/stats/completadas-hoy")
    public ResponseEntity<KpiResponse> getReservasCompletadasHoy(
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(reservaService.getReservasCompletadasHoy(sucursalId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Integer id) {
        reservaService.deleteReserva(id);
        return ResponseEntity.noContent().build();
    }
}
