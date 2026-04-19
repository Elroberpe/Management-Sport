package com.sport.managementsport.identity.service;

import com.sport.managementsport.identity.domain.Cliente;
import com.sport.managementsport.identity.dto.ClienteResponse;
import com.sport.managementsport.identity.dto.CreateClienteRequest;
import com.sport.managementsport.identity.dto.UpdateClienteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {

    ClienteResponse createCliente(CreateClienteRequest request);
    ClienteResponse getClienteById(Integer id);
    Page<ClienteResponse> getAllClientes(String documento, String nombre, Pageable pageable);
    ClienteResponse getClienteByDocumento(String documento);
    Page<ClienteResponse> searchClientes(String query, Pageable pageable);
    ClienteResponse updateCliente(Integer id, UpdateClienteRequest request);
    void deleteCliente(Integer id);

    // Método para uso interno entre servicios
    Cliente findClienteEntityById(Integer id);
}
