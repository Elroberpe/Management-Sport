package com.sport.managementsport.finance.controller;

import com.sport.managementsport.common.enums.MetodoPago;
import com.sport.managementsport.finance.dto.AnularPagoRequest;
import com.sport.managementsport.finance.dto.PagoResponse;
import com.sport.managementsport.finance.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<Page<PagoResponse>> getHistorialFinanciero(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(required = false) MetodoPago metodo,
            @RequestParam(required = false) Integer sucursalId, // <-- Nuevo parámetro
            @PageableDefault(size = 50, sort = "fecha") Pageable pageable) {
        Page<PagoResponse> pagos = pagoService.getAllPagos(desde, hasta, metodo, sucursalId, pageable);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> getPagoById(@PathVariable Integer id) {
        return ResponseEntity.ok(pagoService.getPagoById(id));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<Void> anularPago(@PathVariable Integer id, @Valid @RequestBody AnularPagoRequest request) {
        pagoService.anularPago(id, request);
        return ResponseEntity.noContent().build();
    }
}
