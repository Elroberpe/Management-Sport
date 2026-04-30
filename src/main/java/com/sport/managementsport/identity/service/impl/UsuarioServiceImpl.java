package com.sport.managementsport.identity.service.impl;

import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.repository.UsuarioRepository;
import com.sport.managementsport.identity.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Usuario createUsuario(Usuario usuario) {
        // TODO: Codificar la contraseña antes de guardarla
        // usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario updateUsuario(Integer id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        usuario.setUsername(usuarioDetails.getUsername());
        usuario.setNombre(usuarioDetails.getNombre());
        usuario.setEmail(usuarioDetails.getEmail());
        usuario.setRol(usuarioDetails.getRol());
        
        // Opcional: Manejar el cambio de contraseña
        // if (usuarioDetails.getPassword() != null && !usuarioDetails.getPassword().isEmpty()) {
        //     usuario.setPassword(passwordEncoder.encode(usuarioDetails.getPassword()));
        // }

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void deleteUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
