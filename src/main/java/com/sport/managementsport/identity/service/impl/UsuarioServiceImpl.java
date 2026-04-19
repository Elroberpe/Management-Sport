package com.sport.managementsport.identity.service.impl;

import com.sport.managementsport.common.enums.RolUsuario;
import com.sport.managementsport.company.domain.Empresa;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.repository.EmpresaRepository;
import com.sport.managementsport.company.repository.SucursalRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository, SucursalRepository sucursalRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.sucursalRepository = sucursalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioResponse createUsuario(CreateUsuarioRequest request) {
        // Lógica de validación reutilizada
        validateUsernameUniqueness(request.getUsername(), null);
        validateEmailUniqueness(request.getEmail(), null);

        if ((request.getRol() == RolUsuario.ADMIN || request.getRol() == RolUsuario.RECEPCIONISTA) && request.getSucursalId() == null) {
            throw new BusinessRuleException("La sucursal es obligatoria para los roles ADMIN y RECEPCIONISTA.");
        }

        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + request.getEmpresaId()));

        Sucursal sucursal = null;
        if (request.getSucursalId() != null) {
            sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + request.getSucursalId()));
        }

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);
        usuario.setSucursal(sucursal);
        usuario.setUsername(request.getUsername());
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());

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
    public Page<UsuarioResponse> getAllUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::toUsuarioResponse);
    }

    @Override
    @Transactional
    public UsuarioResponse updateUsuario(Integer id, UpdateUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

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
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

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
        if (!sucursalRepository.existsById(sucursalId)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con id: " + sucursalId);
        }
        return usuarioRepository.findBySucursalSucursalId(sucursalId).stream()
                .map(this::toUsuarioResponse)
                .collect(Collectors.toList());
    }

    // --- Métodos de Validación Privados ---

    private void validateUsernameUniqueness(String username, Integer userIdToIgnore) {
        Optional<Usuario> existingUser = usuarioRepository.findByUsername(username);
        if (existingUser.isPresent() && (userIdToIgnore == null || !existingUser.get().getUsuarioId().equals(userIdToIgnore))) {
            throw new DuplicateResourceException("El nombre de usuario '" + username + "' ya está en uso.");
        }
    }

    private void validateEmailUniqueness(String email, Integer userIdToIgnore) {
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(email);
        if (existingUser.isPresent() && (userIdToIgnore == null || !existingUser.get().getUsuarioId().equals(userIdToIgnore))) {
            throw new DuplicateResourceException("El email '" + email + "' ya está en uso.");
        }
    }

    // --- Mapper ---

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
