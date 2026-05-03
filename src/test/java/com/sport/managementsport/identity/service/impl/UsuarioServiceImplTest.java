package com.sport.managementsport.identity.service.impl;

import com.sport.managementsport.common.enums.RolUsuario;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.dto.UsuarioResponse;
import com.sport.managementsport.identity.repository.UsuarioRepository;
import com.sport.managementsport.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    // @Mock crea una simulación (un objeto falso) de cada dependencia.
    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SucursalService sucursalService;

    @Mock
    private PasswordEncoder passwordEncoder;

    // @InjectMocks ahora tiene todos los mocks que necesita para construir UsuarioServiceImpl.
    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Este método se ejecuta antes de cada @Test.
        // Creamos un objeto de dominio 'Usuario' de ejemplo que usaremos en nuestras pruebas.
        usuario = new Usuario();
        usuario.setUsuarioId(1);
        usuario.setUsername("testuser");
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setRol(RolUsuario.RECEPCIONISTA);
    }

    @Test
    @DisplayName("Prueba que getUsuarioById devuelve un usuario cuando se encuentra")
    void getUsuarioById_ShouldReturnUsuario_WhenFound() {
        // --- 1. Arrange (Organizar) ---
        // Configuramos el comportamiento del mock.
        // "Cuando se llame a usuarioRepository.findById con el ID 1,
        // entonces devuelve un Optional que contiene nuestro usuario de prueba."
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        // --- 2. Act (Actuar) ---
        // Llamamos al método real del servicio que queremos probar.
        UsuarioResponse response = usuarioService.getUsuarioById(1);

        // --- 3. Assert (Afirmar) ---
        // Verificamos que el resultado es el esperado.
        assertNotNull(response); // La respuesta no debe ser nula.
        assertEquals(1, response.getId()); // El ID debe ser 1. <-- LÍNEA CORREGIDA
        assertEquals("testuser", response.getUsername()); // El username debe coincidir.
    }

    @Test
    @DisplayName("Prueba que getUsuarioById lanza una excepción cuando el usuario no se encuentra")
    void getUsuarioById_ShouldThrowException_WhenNotFound() {
        // --- 1. Arrange (Organizar) ---
        // Configuramos el mock para que devuelva un Optional vacío, simulando que no encontró al usuario.
        // anyInt() significa que no importa qué ID le pases, siempre devolverá vacío.
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.empty());

        // --- 2. Act & 3. Assert (Actuar y Afirmar) ---
        // Verificamos que al llamar al método, se lanza la excepción ResourceNotFoundException.
        // Esta es la forma moderna en JUnit 5 de probar excepciones.
        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.getUsuarioById(99); // Usamos un ID que sabemos que no existirá.
        });
    }
}
