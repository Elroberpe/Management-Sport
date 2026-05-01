package com.sport.managementsport.security.controller;

import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.repository.UsuarioRepository;
import com.sport.managementsport.security.JwtService;
import com.sport.managementsport.security.dto.AuthRequest;
import com.sport.managementsport.security.dto.AuthResponse;
import com.sport.managementsport.security.dto.RefreshTokenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var user = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("Error inesperado: usuario no encontrado después de la autenticación"));

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        usuarioRepository.save(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        final String refreshToken = request.getRefreshToken();
        final String username = jwtService.extractUsername(refreshToken);

        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessRuleException("Usuario no encontrado"));

        if (jwtService.isTokenValid(refreshToken, user) && refreshToken.equals(user.getRefreshToken())) {
            var accessToken = jwtService.generateToken(user);
            return ResponseEntity.ok(AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken) // Se devuelve el mismo refresh token
                    .build());
        }

        throw new BusinessRuleException("Refresh token inválido o expirado");
    }
}
