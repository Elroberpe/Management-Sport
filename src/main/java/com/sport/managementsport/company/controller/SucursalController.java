package com.sport.managementsport.company.controller;

import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.service.SucursalService;
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
    public ResponseEntity<Sucursal> createSucursal(@RequestBody Sucursal sucursal) {
        Sucursal newSucursal = sucursalService.createSucursal(sucursal);
        return new ResponseEntity<>(newSucursal, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sucursal> getSucursalById(@PathVariable Integer id) {
        return sucursalService.getSucursalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Sucursal>> getAllSucursales() {
        List<Sucursal> sucursales = sucursalService.getAllSucursales();
        return ResponseEntity.ok(sucursales);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sucursal> updateSucursal(@PathVariable Integer id, @RequestBody Sucursal sucursalDetails) {
        try {
            Sucursal updatedSucursal = sucursalService.updateSucursal(id, sucursalDetails);
            return ResponseEntity.ok(updatedSucursal);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSucursal(@PathVariable Integer id) {
        try {
            sucursalService.deleteSucursal(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
