package com.sport.managementsport.company.controller;

import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.company.service.CanchaService;
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
    public ResponseEntity<Cancha> createCancha(@RequestBody Cancha cancha) {
        Cancha newCancha = canchaService.createCancha(cancha);
        return new ResponseEntity<>(newCancha, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cancha> getCanchaById(@PathVariable Integer id) {
        return canchaService.getCanchaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Cancha>> getAllCanchas() {
        List<Cancha> canchas = canchaService.getAllCanchas();
        return ResponseEntity.ok(canchas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cancha> updateCancha(@PathVariable Integer id, @RequestBody Cancha canchaDetails) {
        try {
            Cancha updatedCancha = canchaService.updateCancha(id, canchaDetails);
            return ResponseEntity.ok(updatedCancha);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCancha(@PathVariable Integer id) {
        try {
            canchaService.deleteCancha(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
