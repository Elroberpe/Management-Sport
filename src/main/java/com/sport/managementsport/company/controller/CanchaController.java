package com.sport.managementsport.company.controller;

import com.sport.managementsport.common.enums.EstadoCancha;
import com.sport.managementsport.company.dto.CanchaResponse;
import com.sport.managementsport.company.dto.CreateCanchaRequest;
import com.sport.managementsport.company.dto.UpdateCanchaRequest;
import com.sport.managementsport.company.dto.UpdateEstadoCanchaRequest;
import com.sport.managementsport.company.service.CanchaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/canchas")
public class CanchaController {

    private final CanchaService canchaService;

    public CanchaController(CanchaService canchaService) {
        this.canchaService = canchaService;
    }

    @PostMapping
    public ResponseEntity<CanchaResponse> createCancha(@Valid @RequestBody CreateCanchaRequest request) {
        CanchaResponse newCancha = canchaService.createCancha(request);
        return new ResponseEntity<>(newCancha, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CanchaResponse> getCanchaById(@PathVariable Integer id) {
        return canchaService.getCanchaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CanchaResponse>> getAllCanchas(
            @RequestParam(required = false) Integer sucursalId,
            @RequestParam(required = false) EstadoCancha estado) {
        List<CanchaResponse> canchas = canchaService.getAllCanchas(sucursalId, estado);
        return ResponseEntity.ok(canchas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CanchaResponse> updateCancha(@PathVariable Integer id, @Valid @RequestBody UpdateCanchaRequest request) {
        CanchaResponse updatedCancha = canchaService.updateCancha(id, request);
        return ResponseEntity.ok(updatedCancha);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CanchaResponse> updateEstadoCancha(@PathVariable Integer id, @Valid @RequestBody UpdateEstadoCanchaRequest request) {
        CanchaResponse updatedCancha = canchaService.updateEstadoCancha(id, request);
        return ResponseEntity.ok(updatedCancha);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCancha(@PathVariable Integer id) {
        canchaService.deleteCancha(id);
        return ResponseEntity.noContent().build();
    }
}
