package com.sport.managementsport.identity.controller;

import com.sport.managementsport.common.enums.RolUsuario;
import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// --- ANOTACIONES CLAVE DE PRUEBAS DE INTEGRACIÓN ---

// @SpringBootTest: Levanta un contexto completo de la aplicación Spring Boot.
// Es como encender tu aplicación, pero para pruebas.
// WebEnvironment.MOCK simula el entorno web sin necesidad de un servidor real.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

// @AutoConfigureMockMvc: Configura automáticamente el objeto MockMvc,
// que es nuestra herramienta para hacer peticiones HTTP simuladas.
@AutoConfigureMockMvc

// @ActiveProfiles("test"): Le dice a Spring que active el perfil "test".
// Esto es útil si quisiéramos tener un application-test.properties para configuraciones específicas de prueba.
@ActiveProfiles("test")
class UsuarioControllerIT {

    // Inyecta el MockMvc configurado por Spring.
    @Autowired
    private MockMvc mockMvc;

    // Inyecta el repositorio real para poder preparar datos en la BD de prueba (H2).
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Inyecta el PasswordEncoder real para crear usuarios con contraseñas válidas.
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario user1, user2;

    @BeforeEach
    void setUp() {
        // Antes de cada prueba, creamos dos usuarios de ejemplo.
        user1 = new Usuario();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("password"));
        user1.setRol(RolUsuario.RECEPCIONISTA);
        user1.setNombre("User One");
        user1.setEmail("user1@test.com");

        user2 = new Usuario();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("password"));
        user2.setRol(RolUsuario.RECEPCIONISTA);
        user2.setNombre("User Two");
        user2.setEmail("user2@test.com");

        // Guardamos los usuarios en la base de datos de prueba (H2).
        // El repositorio nos devuelve las entidades con los IDs ya asignados.
        user1 = usuarioRepository.save(user1);
        user2 = usuarioRepository.save(user2);
    }

    @AfterEach
    void tearDown() {
        // Después de cada prueba, limpiamos la base de datos para no afectar a la siguiente prueba.
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Un RECEPCIONISTA no puede ver el perfil de otro usuario (debe dar 403 Forbidden)")
    void getUsuarioById_ShouldReturn403_WhenUserIsDifferent() throws Exception {
        // --- Arrange (Organizar) ---
        // No hay nada que organizar aquí, el setUp() ya preparó la base de datos.

        // --- Act & Assert (Actuar y Afirmar) ---
        mockMvc.perform(get("/api/v1/usuarios/" + user2.getUsuarioId()) // Intentamos acceder al perfil de user2...
                        .with(user(user1))) // ...autenticados como user1.
                .andExpect(status().isForbidden()); // ...y esperamos un estado HTTP 403 (Forbidden).
    }

    @Test
    @DisplayName("Un RECEPCIONISTA puede ver su propio perfil (debe dar 200 OK)")
    void getUsuarioById_ShouldReturn200_WhenUserIsSame() throws Exception {
        // --- Arrange ---
        // El setUp() ya preparó la base de datos.

        // --- Act & Assert ---
        mockMvc.perform(get("/api/v1/usuarios/" + user1.getUsuarioId()) // Intentamos acceder al perfil de user1...
                        .with(user(user1))) // ...autenticados como el mismo user1.
                .andExpect(status().isOk()); // ...y esperamos un estado HTTP 200 (OK).
    }
}
