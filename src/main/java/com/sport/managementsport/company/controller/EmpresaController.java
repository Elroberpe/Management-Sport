package com.sport.managementsport.company.controller;

import com.sport.managementsport.company.dto.CreateEmpresaRequest;
import com.sport.managementsport.company.dto.EmpresaResponse;
import com.sport.managementsport.company.dto.SucursalResponse;
import com.sport.managementsport.company.dto.UpdateEmpresaRequest;
import com.sport.managementsport.company.service.EmpresaService;
import com.sport.managementsport.company.service.SucursalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final SucursalService sucursalService;

    public EmpresaController(EmpresaService empresaService, SucursalService sucursalService) {
        this.empresaService = empresaService;
        this.sucursalService = sucursalService;
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> createEmpresa(@Valid @RequestBody CreateEmpresaRequest request) {
        EmpresaResponse newEmpresa = empresaService.createEmpresa(request);
        return new ResponseEntity<>(newEmpresa, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> getEmpresaById(@PathVariable Integer id) {
        return empresaService.getEmpresaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> getAllEmpresas() {
        List<EmpresaResponse> empresas = empresaService.getAllEmpresas();
        return ResponseEntity.ok(empresas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponse> updateEmpresa(@PathVariable Integer id, @Valid @RequestBody UpdateEmpresaRequest request) {
        EmpresaResponse updatedEmpresa = empresaService.updateEmpresa(id, request);
        return ResponseEntity.ok(updatedEmpresa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Integer id) {
        empresaService.deleteEmpresa(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{empresaId}/sucursales")
    public ResponseEntity<List<SucursalResponse>> getSucursalesByEmpresa(@PathVariable Integer empresaId) {
        List<SucursalResponse> sucursales = sucursalService.getSucursalesByEmpresaId(empresaId);
        return ResponseEntity.ok(sucursales);
    }
}
