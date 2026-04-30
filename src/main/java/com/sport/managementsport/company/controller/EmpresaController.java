package com.sport.managementsport.company.controller;

import com.sport.managementsport.company.dto.CreateEmpresaRequest;
import com.sport.managementsport.company.dto.EmpresaResponse;
import com.sport.managementsport.company.dto.UpdateEmpresaRequest;
import com.sport.managementsport.company.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<EmpresaResponse> createEmpresa(@Valid @RequestBody CreateEmpresaRequest request) {
        EmpresaResponse newEmpresa = empresaService.createEmpresa(request);
        return new ResponseEntity<>(newEmpresa, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<EmpresaResponse>> getAllEmpresas() {
        return ResponseEntity.ok(empresaService.getAllEmpresas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<EmpresaResponse> getEmpresaById(@PathVariable Integer id) {
        EmpresaResponse empresa = empresaService.getEmpresaById(id);
        return ResponseEntity.ok(empresa);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<EmpresaResponse> updateEmpresa(@PathVariable Integer id, @Valid @RequestBody UpdateEmpresaRequest request) {
        EmpresaResponse updatedEmpresa = empresaService.updateEmpresa(id, request);
        return ResponseEntity.ok(updatedEmpresa);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Integer id) {
        empresaService.deleteEmpresa(id);
        return ResponseEntity.noContent().build();
    }
}
