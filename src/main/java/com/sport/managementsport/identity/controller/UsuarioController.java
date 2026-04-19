package com.sport.managementsport.identity.controller;

import com.sport.managementsport.identity.dto.ChangePasswordRequest;
import com.sport.managementsport.identity.dto.CreateUsuarioRequest;
import com.sport.managementsport.identity.dto.UpdateUsuarioRequest;
import com.sport.managementsport.identity.dto.UsuarioResponse;
import com.sport.managementsport.identity.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody CreateUsuarioRequest request) {
        UsuarioResponse newUsuario = usuarioService.createUsuario(request);
        return new ResponseEntity<>(newUsuario, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getUsuarioById(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> getAllUsuarios(@PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(usuarioService.getAllUsuarios(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable Integer id, @Valid @RequestBody UpdateUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.updateUsuario(id, request));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Integer id, @Valid @RequestBody ChangePasswordRequest request) {
        usuarioService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
