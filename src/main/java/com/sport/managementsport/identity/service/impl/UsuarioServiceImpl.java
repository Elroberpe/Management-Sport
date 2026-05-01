package com.sport.managementsport.identity.service.impl;

import com.sport.managementsport.common.enums.RolUsuario;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.dto.ChangePasswordRequest;
import com.sport.managementsport.identity.dto.CreateUsuarioRequest;
import com.sport.managementsport.identity.dto.UpdateUsuarioRequest;
import com.sport.managementsport.identity.dto.UsuarioResponse;
import com.sport.managementsport.identity.repository.UsuarioRepository;
import com.sport.managementsport.identity.service.UsuarioService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.DuplicateResourceException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SucursalService sucursalService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, @Lazy SucursalService sucursalService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.sucursalService = sucursalService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));
    }

    @Override
    @Transactional
    public UsuarioResponse createUsuario(CreateUsuarioRequest request) {
        // Usando los métodos de validación optimizados
        validateUsernameUniqueness(request.getUsername(), null);
        validateEmailUniqueness(request.getEmail(), null);

        Usuario currentUser = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RolUsuario currentUserRole = currentUser.getRol();
        RolUsuario roleToAssign = request.getRol();

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

        Sucursal sucursal = null;
        if (request.getSucursalId() != null) {
            sucursal = sucursalService.findSucursalEntityById(request.getSucursalId());
        }

        if (roleToAssign == RolUsuario.ADMIN && sucursal == null) {
            throw new BusinessRuleException("Para crear un ADMIN, es obligatorio especificar la sucursal que va a gestionar.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmpresa(currentUser.getEmpresa());
        usuario.setSucursal(sucursal);
        usuario.setUsername(request.getUsername());
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(roleToAssign);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return toUsuarioResponse(savedUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse getUsuarioById(Integer id) {
        return usuarioRepository.findById(id)
                .map(this::toUsuarioResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> getAllUsuarios(Pageable pageable, Usuario currentUser) {
        if (currentUser.getRol() == RolUsuario.SUPERADMIN) {
            return usuarioRepository.findAll(pageable).map(this::toUsuarioResponse);
        } else if (currentUser.getRol() == RolUsuario.ADMIN) {
            Integer sucursalId = currentUser.getSucursal().getSucursalId();
            return usuarioRepository.findBySucursalSucursalId(sucursalId, pageable).map(this::toUsuarioResponse);
        }
        // Por defecto, si no es ninguno de los anteriores (ej. RECEPCIONISTA), devuelve una página vacía.
        return Page.empty(pageable);
    }

    @Override
    @Transactional
    public UsuarioResponse updateUsuario(Integer id, UpdateUsuarioRequest request) {
        Usuario usuario = findUsuarioEntityById(id);

        if (request.getUsername() != null) {
            validateUsernameUniqueness(request.getUsername(), id);
            usuario.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            validateEmailUniqueness(request.getEmail(), id);
            usuario.setEmail(request.getEmail());
        }

        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return toUsuarioResponse(updatedUsuario);
    }

    @Override
    @Transactional
    public void changePassword(Integer id, ChangePasswordRequest request) {
        Usuario usuario = findUsuarioEntityById(id);

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BusinessRuleException("La contraseña actual es incorrecta.");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void deleteUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getUsuariosBySucursalId(Integer sucursalId) {
        if (!sucursalService.sucursalExists(sucursalId)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con id: " + sucursalId);
        }
        return usuarioRepository.findBySucursalSucursalId(sucursalId).stream()
                .map(this::toUsuarioResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findUsuarioEntityById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    private void validateUsernameUniqueness(String username, Integer userIdToIgnore) {
        if (usuarioRepository.existsByUsername(username)) {
            if (userIdToIgnore != null) {
                // Si estamos actualizando, solo lanzamos error si el username pertenece a OTRO usuario.
                Usuario existingUser = usuarioRepository.findByUsername(username).get();
                if (!existingUser.getUsuarioId().equals(userIdToIgnore)) {
                    throw new DuplicateResourceException("El nombre de usuario '" + username + "' ya está en uso.");
                }
            } else {
                // Si estamos creando, cualquier existencia es un error.
                throw new DuplicateResourceException("El nombre de usuario '" + username + "' ya está en uso.");
            }
        }
    }

    private void validateEmailUniqueness(String email, Integer userIdToIgnore) {
        if (usuarioRepository.existsByEmail(email)) {
            if (userIdToIgnore != null) {
                Usuario existingUser = usuarioRepository.findByEmail(email).get();
                if (!existingUser.getUsuarioId().equals(userIdToIgnore)) {
                    throw new DuplicateResourceException("El email '" + email + "' ya está en uso.");
                }
            } else {
                throw new DuplicateResourceException("El email '" + email + "' ya está en uso.");
            }
        }
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getUsuarioId(),
                usuario.getEmpresa() != null ? usuario.getEmpresa().getEmpresaId() : null,
                usuario.getSucursal() != null ? usuario.getSucursal().getSucursalId() : null,
                usuario.getUsername(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getUltimoLogin()
        );
    }
}
