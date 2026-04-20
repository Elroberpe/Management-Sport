package com.sport.managementsport.company.controller;

import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.dto.UpdateSucursalRequest;
import com.sport.managementsport.company.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping
    public ResponseEntity<SucursalResponse> createSucursal(@Valid @RequestBody CreateSucursalRequest request) {
        SucursalResponse newSucursal = sucursalService.createSucursal(request);
        return new ResponseEntity<>(newSucursal, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SucursalResponse>> getAllSucursales(@RequestParam(required = false) Integer empresaId) {
        List<SucursalResponse> sucursales;
        if (empresaId != null) {
            sucursales = sucursalService.getSucursalesByEmpresaId(empresaId);
        } else {
            sucursales = sucursalService.getAllSucursales();
        }
        return ResponseEntity.ok(sucursales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponse> getSucursalById(@PathVariable Integer id) {
        SucursalResponse sucursal = sucursalService.getSucursalById(id);
        return ResponseEntity.ok(sucursal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponse> updateSucursal(@PathVariable Integer id, @Valid @RequestBody UpdateSucursalRequest request) {
        SucursalResponse updatedSucursal = sucursalService.updateSucursal(id, request);
        return ResponseEntity.ok(updatedSucursal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSucursal(@PathVariable Integer id) {
        sucursalService.deleteSucursal(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<SucursalResponse> activarSucursal(@PathVariable Integer id) {
        return ResponseEntity.ok(sucursalService.activarSucursal(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<SucursalResponse> desactivarSucursal(@PathVariable Integer id) {
        return ResponseEntity.ok(sucursalService.desactivarSucursal(id));
    }
}
