package com.sport.managementsport.identity.service;

import com.sport.managementsport.identity.domain.Usuario;
import com.sport.managementsport.identity.dto.ChangePasswordRequest;
import com.sport.managementsport.identity.dto.CreateUsuarioRequest;
import com.sport.managementsport.identity.dto.UpdateUsuarioRequest;
import com.sport.managementsport.identity.dto.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsuarioService extends UserDetailsService {

    UsuarioResponse createUsuario(CreateUsuarioRequest request);

    UsuarioResponse getUsuarioById(Integer id);

    Page<UsuarioResponse> getAllUsuarios(Pageable pageable, Usuario currentUser);

    UsuarioResponse updateUsuario(Integer id, UpdateUsuarioRequest request);

    void changePassword(Integer id, ChangePasswordRequest request);

    void deleteUsuario(Integer id);

    List<UsuarioResponse> getUsuariosBySucursalId(Integer sucursalId);

    Usuario findUsuarioEntityById(Integer id);
}
