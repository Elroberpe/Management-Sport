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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody CreateUsuarioRequest request) {
        // Lógica de servicio debe validar que un ADMIN solo puede crear RECEPCIONISTAS para su sede.
        UsuarioResponse newUsuario = usuarioService.createUsuario(request);
        return new ResponseEntity<>(newUsuario, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')") // Un usuario también debería poder ver su propio perfil.
    public ResponseEntity<UsuarioResponse> getUsuarioById(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Page<UsuarioResponse>> getAllUsuarios(@PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        // Lógica de servicio debe filtrar por sede si el usuario es ADMIN.
        return ResponseEntity.ok(usuarioService.getAllUsuarios(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')") // Un usuario también debería poder actualizar su propio perfil.
    public ResponseEntity<UsuarioResponse> updateUsuario(@PathVariable Integer id, @Valid @RequestBody UpdateUsuarioRequest request) {
        // Lógica de servicio debe validar que un ADMIN solo puede actualizar usuarios de su sede.
        return ResponseEntity.ok(usuarioService.updateUsuario(id, request));
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')") // Un usuario también debería poder cambiar su propia contraseña.
    public ResponseEntity<Void> changePassword(@PathVariable Integer id, @Valid @RequestBody ChangePasswordRequest request) {
        // Lógica de servicio debe validar que un ADMIN solo puede cambiar la contraseña de usuarios de su sede.
        usuarioService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
        // Lógica de servicio debe validar que un ADMIN solo puede eliminar usuarios de su sede.
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
