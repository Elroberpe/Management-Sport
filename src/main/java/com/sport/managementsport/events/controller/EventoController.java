package com.sport.managementsport.events.controller;

import com.sport.managementsport.events.dto.*;
import com.sport.managementsport.events.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @PostMapping
    public ResponseEntity<EventoResponse> createEvento(@Valid @RequestBody CreateEventoRequest request) {
        EventoResponse nuevoEvento = eventoService.createEvento(request);
        return new ResponseEntity<>(nuevoEvento, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> getEventoById(@PathVariable Integer id) {
        return ResponseEntity.ok(eventoService.getEventoById(id));
    }

    @GetMapping
    public ResponseEntity<Page<EventoResponse>> getAllEventos(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventoService.getAllEventos(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> updateEvento(@PathVariable Integer id, @Valid @RequestBody UpdateEventoRequest request) {
        return ResponseEntity.ok(eventoService.updateEvento(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<EventoResponse> cancelarEvento(@PathVariable Integer id, @Valid @RequestBody CancelEventoRequest request) {
        return ResponseEntity.ok(eventoService.cancelarEvento(id, request));
    }

    @PostMapping("/{id}/pagos")
    public ResponseEntity<EventoResponse> addPago(@PathVariable Integer id, @Valid @RequestBody AddPagoToEventoRequest request) {
        return ResponseEntity.ok(eventoService.addPago(id, request));
    }

    @PostMapping("/{id}/reprogramar")
    public ResponseEntity<EventoResponse> reprogramarEvento(@PathVariable Integer id, @Valid @RequestBody ReprogramarEventoRequest request) {
        return ResponseEntity.ok(eventoService.reprogramarEvento(id, request));
    }
}
