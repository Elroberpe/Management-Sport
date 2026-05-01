package com.sport.managementsport.security.service;

import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.repository.UsuarioRepository; // Importar Repository
import com.sport.managementsport.security.JwtService;
import com.sport.managementsport.security.dto.AuthRequest;
import com.sport.managementsport.security.dto.AuthResponse;
import com.sport.managementsport.security.dto.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService; // Importar UserDetailsService
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService; // Para cargar el usuario
    private final UsuarioRepository usuarioRepository; // Para guardar el refresh token
    private final JwtService jwtService;

    @Transactional
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Usuario user = (Usuario) userDetailsService.loadUserByUsername(request.getUsername());

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        usuarioRepository.save(user); // AuthService gestiona el estado de la sesión

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        final String refreshToken = request.getRefreshToken();
        final String username = jwtService.extractUsername(refreshToken);

        Usuario user = (Usuario) userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(refreshToken, user) && refreshToken.equals(user.getRefreshToken())) {
            var accessToken = jwtService.generateToken(user);
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

        throw new BusinessRuleException("Refresh token inválido o expirado");
    }

    @Transactional
    public void logout(Authentication authentication) {
        if (authentication == null) {
            return;
        }

        String username = authentication.getName();
        usuarioRepository.findByUsername(username).ifPresent(user -> {
            user.setRefreshToken(null);
            usuarioRepository.save(user); // AuthService gestiona el estado de la sesión
        });
    }
}
