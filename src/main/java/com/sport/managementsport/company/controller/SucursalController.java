package com.sport.managementsport.company.controller;

import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.dto.UpdateSucursalRequest;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.company.dto.CanchaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<SucursalResponse> createSucursal(@Valid @RequestBody CreateSucursalRequest request) {
        SucursalResponse newSucursal = sucursalService.createSucursal(request);
        return new ResponseEntity<>(newSucursal, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<SucursalResponse>> getAllSucursales(@RequestParam Integer empresaId) {
        List<SucursalResponse> sucursales = sucursalService.getAllSucursales(empresaId);
        return ResponseEntity.ok(sucursales);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<SucursalResponse> getSucursalById(@PathVariable Integer id) {
        SucursalResponse sucursal = sucursalService.getSucursalById(id);
        return ResponseEntity.ok(sucursal);
    }

    // Endpoint para obtener las canchas de una sucursal específica
    @GetMapping("/{id}/canchas")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<CanchaResponse>> getCanchasBySucursal(@PathVariable Integer id) {
        // TODO: Asegurar que el usuario logueado (si es ADMIN/RECEPTIONIST) 
        // solo pueda consultar las canchas de SU propia sucursal.
        List<CanchaResponse> canchas = sucursalService.getCanchasBySucursalId(id);
        return ResponseEntity.ok(canchas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<SucursalResponse> updateSucursal(@PathVariable Integer id, @Valid @RequestBody UpdateSucursalRequest request) {
        SucursalResponse updatedSucursal = sucursalService.updateSucursal(id, request);
        return ResponseEntity.ok(updatedSucursal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deleteSucursal(@PathVariable Integer id) {
        sucursalService.deleteSucursal(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<SucursalResponse> activarSucursal(@PathVariable Integer id) {
        return ResponseEntity.ok(sucursalService.activarSucursal(id));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<SucursalResponse> desactivarSucursal(@PathVariable Integer id) {
        return ResponseEntity.ok(sucursalService.desactivarSucursal(id));
    }
}
