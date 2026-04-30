package com.sport.managementsport.identity.controller;

import com.sport.managementsport.identity.dto.ClienteResponse;
import com.sport.managementsport.identity.dto.CreateClienteRequest;
import com.sport.managementsport.identity.dto.UpdateClienteRequest;
import com.sport.managementsport.identity.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ClienteResponse> createCliente(@Valid @RequestBody CreateClienteRequest request) {
        ClienteResponse newCliente = clienteService.createCliente(request);
        return new ResponseEntity<>(newCliente, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ClienteResponse> getClienteById(@PathVariable Integer id) {
        ClienteResponse cliente = clienteService.getClienteById(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Page<ClienteResponse>> getAllClientes(
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<ClienteResponse> clientes = clienteService.getAllClientes(documento, nombre, pageable);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/documento/{documento}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ClienteResponse> getClienteByDocumento(@PathVariable String documento) {
        ClienteResponse cliente = clienteService.getClienteByDocumento(documento);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Page<ClienteResponse>> searchClientes(
            @RequestParam(name = "q") String query,
            @PageableDefault(size = 5) Pageable pageable) {
        Page<ClienteResponse> clientes = clienteService.searchClientes(query, pageable);
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ClienteResponse> updateCliente(@PathVariable Integer id, @Valid @RequestBody UpdateClienteRequest request) {
        ClienteResponse updatedCliente = clienteService.updateCliente(id, request);
        return ResponseEntity.ok(updatedCliente);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteCliente(@PathVariable Integer id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
}
