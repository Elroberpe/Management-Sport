package com.sport.managementsport.identity.service;

import com.sport.managementsport.identity.domain.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    Usuario createUsuario(Usuario usuario);

    Optional<Usuario> getUsuarioById(Integer id);

    List<Usuario> getAllUsuarios();

    Usuario updateUsuario(Integer id, Usuario usuarioDetails);

    void deleteUsuario(Integer id);
}
