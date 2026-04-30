package com.sport.managementsport.events.controller;

import com.sport.managementsport.events.domain.Mantenimiento;
import com.sport.managementsport.events.service.MantenimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mantenimientos")
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    @PostMapping
    public ResponseEntity<Mantenimiento> createMantenimiento(@RequestBody Mantenimiento mantenimiento) {
        Mantenimiento newMantenimiento = mantenimientoService.createMantenimiento(mantenimiento);
        return new ResponseEntity<>(newMantenimiento, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mantenimiento> getMantenimientoById(@PathVariable Integer id) {
        return mantenimientoService.getMantenimientoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Mantenimiento>> getAllMantenimientos() {
        List<Mantenimiento> mantenimientos = mantenimientoService.getAllMantenimientos();
        return ResponseEntity.ok(mantenimientos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mantenimiento> updateMantenimiento(@PathVariable Integer id, @RequestBody Mantenimiento mantenimientoDetails) {
        try {
            Mantenimiento updatedMantenimiento = mantenimientoService.updateMantenimiento(id, mantenimientoDetails);
            return ResponseEntity.ok(updatedMantenimiento);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMantenimiento(@PathVariable Integer id) {
        try {
            mantenimientoService.deleteMantenimiento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
