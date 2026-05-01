package com.sport.managementsport.identity.repository;

import com.sport.managementsport.identity.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findBySucursalSucursalId(Integer sucursalId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
