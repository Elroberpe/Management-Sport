package com.sport.managementsport.company.controller;

import com.sport.managementsport.company.dto.CanchaResponse;
import com.sport.managementsport.company.dto.CreateSucursalRequest;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.dto.UpdateSucursalRequest;
import com.sport.managementsport.company.service.CanchaService;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.identity.dto.UsuarioResponse;
import com.sport.managementsport.identity.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;
    private final CanchaService canchaService;
    private final UsuarioService usuarioService;

    public SucursalController(SucursalService sucursalService, CanchaService canchaService, UsuarioService usuarioService) {
        this.sucursalService = sucursalService;
        this.canchaService = canchaService;
        this.usuarioService = usuarioService;
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
        SucursalResponse sucursal = sucursalService.activarSucursal(id);
        return ResponseEntity.ok(sucursal);
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<SucursalResponse> desactivarSucursal(@PathVariable Integer id) {
        SucursalResponse sucursal = sucursalService.desactivarSucursal(id);
        return ResponseEntity.ok(sucursal);
    }

    // --- Endpoints Anidados ---

    @GetMapping("/{sucursalId}/canchas")
    public ResponseEntity<List<CanchaResponse>> getCanchasBySucursal(@PathVariable Integer sucursalId) {
        List<CanchaResponse> canchas = canchaService.getCanchasBySucursalId(sucursalId);
        return ResponseEntity.ok(canchas);
    }

    @GetMapping("/{sucursalId}/usuarios")
    public ResponseEntity<List<UsuarioResponse>> getUsuariosBySucursal(@PathVariable Integer sucursalId) {
        List<UsuarioResponse> usuarios = usuarioService.getUsuariosBySucursalId(sucursalId);
        return ResponseEntity.ok(usuarios);
    }
}
