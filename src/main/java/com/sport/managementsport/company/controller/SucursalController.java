package com.sport.managementsport.company.controller;

import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.service.SucursalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @PostMapping
    public ResponseEntity<SucursalResponse> createSucursal(@Valid @RequestBody CreateSucursalRequest request) {
        SucursalResponse newSucursal = sucursalService.createSucursal(request);
        return new ResponseEntity<>(newSucursal, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponse> getSucursalById(@PathVariable Integer id) {
        return sucursalService.getSucursalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SucursalResponse>> getAllSucursales() {
        List<SucursalResponse> sucursales = sucursalService.getAllSucursales();
        return ResponseEntity.ok(sucursales);
    }

    /*
    // Dejamos el update comentado hasta que creemos el UpdateSucursalRequest
    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponse> updateSucursal(@PathVariable Integer id, @Valid @RequestBody UpdateSucursalRequest request) {
        SucursalResponse updatedSucursal = sucursalService.updateSucursal(id, request);
        return ResponseEntity.ok(updatedSucursal);
    }
    */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSucursal(@PathVariable Integer id) {
        sucursalService.deleteSucursal(id);
        return ResponseEntity.noContent().build();
    }
}
