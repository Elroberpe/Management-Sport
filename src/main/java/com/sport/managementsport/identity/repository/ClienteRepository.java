package com.sport.managementsport.identity.repository;

import com.sport.managementsport.identity.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer>, JpaSpecificationExecutor<Cliente> {
    boolean existsByDocumento(String documento);
    Optional<Cliente> findByDocumento(String documento);
}
