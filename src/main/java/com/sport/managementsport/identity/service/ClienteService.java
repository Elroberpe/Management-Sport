package com.sport.managementsport.identity.service;

import com.sport.managementsport.identity.domain.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {

    Cliente createCliente(Cliente cliente);

    Optional<Cliente> getClienteById(Integer id);

    List<Cliente> getAllClientes();

    Cliente updateCliente(Integer id, Cliente clienteDetails);

    void deleteCliente(Integer id);
}
