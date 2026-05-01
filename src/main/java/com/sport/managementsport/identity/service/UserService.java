package com.sport.managementsport.identity.service;

import com.sport.managementsport.common.enums.RolUsuario;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.repository.SucursalRepository;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.DuplicateResourceException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.dto.CreateUserRequest;
import com.sport.managementsport.identity.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SucursalRepository sucursalRepository;

    @Transactional
    public void createUser(CreateUserRequest request) {
        // 1. Obtener el usuario autenticado que realiza la operación
        Usuario currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RolUsuario currentUserRole = currentUser.getRol();
        RolUsuario roleToAssign = request.getRol();

        // 2. Validar que el username y email no existan
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("El nombre de usuario '" + request.getUsername() + "' ya está en uso.");
        }
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("El email '" + request.getEmail() + "' ya está registrado.");
        }

        // 3. Aplicar reglas de negocio basadas en roles
        switch (currentUserRole) {
            case ADMIN:
                if (roleToAssign != RolUsuario.RECEPCIONISTA) {
                    throw new AccessDeniedException("Un ADMIN solo puede crear usuarios con rol RECEPCIONISTA.");
                }
                break;
            case SUPERADMIN:
                if (roleToAssign == RolUsuario.SUPERADMIN) {
                    throw new BusinessRuleException("No se puede crear más de un SUPERADMIN.");
                }
                break;
            case RECEPCIONISTA:
                throw new AccessDeniedException("Un RECEPCIONISTA no puede crear usuarios.");
        }

        // 4. Validar y obtener la sucursal
        Sucursal sucursalAsignada = null;
        if (request.getSucursalId() != null) {
            sucursalAsignada = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID '" + request.getSucursalId() + "' no existe."));
        }

        // 5. Aplicar regla de negocio: un ADMIN debe tener una sucursal
        if (roleToAssign == RolUsuario.ADMIN && sucursalAsignada == null) {
            throw new BusinessRuleException("Para crear un ADMIN, es obligatorio especificar la sucursal que va a gestionar.");
        }

        // 6. Crear y guardar el nuevo usuario
        Usuario newUser = new Usuario();
        newUser.setUsername(request.getUsername());
        newUser.setNombre(request.getNombre());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRol(roleToAssign);
        newUser.setSucursal(sucursalAsignada);
        newUser.setEmpresa(currentUser.getEmpresa()); // Asignar la empresa del usuario actual

        usuarioRepository.save(newUser);
    }
}
