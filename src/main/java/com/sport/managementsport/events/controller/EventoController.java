package com.sport.managementsport.events.controller;

import com.sport.managementsport.events.dto.*;
import com.sport.managementsport.events.service.EventoService;
import com.sport.managementsport.finance.dto.PagoResponse;
import com.sport.managementsport.finance.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final PagoService pagoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<EventoResponse> createEvento(@Valid @RequestBody CreateEventoRequest request) {
        EventoResponse nuevoEvento = eventoService.createEvento(request);
        return new ResponseEntity<>(nuevoEvento, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<EventoResponse> getEventoById(@PathVariable Integer id) {
        return ResponseEntity.ok(eventoService.getEventoById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<Page<EventoResponse>> getAllEventos(
            @RequestParam(required = false) Integer sucursalId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventoService.getAllEventos(sucursalId, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<EventoResponse> updateEvento(@PathVariable Integer id, @Valid @RequestBody UpdateEventoRequest request) {
        return ResponseEntity.ok(eventoService.updateEvento(id, request));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<EventoResponse> cancelarEvento(@PathVariable Integer id, @Valid @RequestBody CancelEventoRequest request) {
        return ResponseEntity.ok(eventoService.cancelarEvento(id, request));
    }

    @GetMapping("/{id}/simular-cancelacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<SimulacionCancelacionResponse> simularCancelacion(@PathVariable Integer id) {
        return ResponseEntity.ok(eventoService.simularCancelacion(id));
    }

    @PostMapping("/{id}/pagos")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<EventoResponse> addPago(@PathVariable Integer id, @Valid @RequestBody AddPagoToEventoRequest request) {
        return ResponseEntity.ok(eventoService.addPago(id, request));
    }

    @GetMapping("/{id}/pagos")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<List<PagoResponse>> getPagosDelEvento(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoService.getPagosByEventoId(id));
    }

    @PostMapping("/{id}/reprogramar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<EventoResponse> reprogramarEvento(@PathVariable Integer id, @Valid @RequestBody ReprogramarEventoRequest request) {
        return ResponseEntity.ok(eventoService.reprogramarEvento(id, request));
    }
}
