package com.sport.managementsport.events.controller;

import com.sport.managementsport.events.dto.CreateMantenimientoRequest;
import com.sport.managementsport.events.dto.MantenimientoResponse;
import com.sport.managementsport.events.dto.UpdateEstadoMantenimientoRequest;
import com.sport.managementsport.events.dto.UpdateMantenimientoRequest;
import com.sport.managementsport.events.service.MantenimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mantenimientos")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    @PostMapping
    public ResponseEntity<MantenimientoResponse> createMantenimiento(@Valid @RequestBody CreateMantenimientoRequest request) {
        MantenimientoResponse newMantenimiento = mantenimientoService.createMantenimiento(request);
        return new ResponseEntity<>(newMantenimiento, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoResponse> getMantenimientoById(@PathVariable Integer id) {
        return ResponseEntity.ok(mantenimientoService.getMantenimientoById(id));
    }

    @GetMapping
    public ResponseEntity<Page<MantenimientoResponse>> getAllMantenimientos(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(mantenimientoService.getAllMantenimientos(pageable));
    }

    @GetMapping("/cancha/{canchaId}")
    public ResponseEntity<List<MantenimientoResponse>> getMantenimientosByCancha(@PathVariable Integer canchaId) {
        return ResponseEntity.ok(mantenimientoService.getMantenimientosByCanchaId(canchaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoResponse> updateMantenimiento(@PathVariable Integer id, @Valid @RequestBody UpdateMantenimientoRequest request) {
        return ResponseEntity.ok(mantenimientoService.updateMantenimiento(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<MantenimientoResponse> updateEstado(@PathVariable Integer id, @Valid @RequestBody UpdateEstadoMantenimientoRequest request) {
        return ResponseEntity.ok(mantenimientoService.updateEstado(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<MantenimientoResponse> cancelarMantenimiento(@PathVariable Integer id) {
        return ResponseEntity.ok(mantenimientoService.cancelarMantenimiento(id));
    }
}
