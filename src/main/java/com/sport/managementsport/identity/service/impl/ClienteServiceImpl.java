package com.sport.managementsport.identity.service.impl;

import com.sport.managementsport.identity.domain.Cliente;
import com.sport.managementsport.identity.dto.ClienteResponse;
import com.sport.managementsport.identity.dto.CreateClienteRequest;
import com.sport.managementsport.identity.dto.UpdateClienteRequest;
import com.sport.managementsport.identity.repository.ClienteRepository;
import com.sport.managementsport.identity.repository.ClienteSpecification;
import com.sport.managementsport.identity.service.ClienteService;
import com.sport.managementsport.exception.DuplicateResourceException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public ClienteResponse createCliente(CreateClienteRequest request) {
        if (clienteRepository.existsByDocumento(request.getDocumento())) {
            throw new DuplicateResourceException("El documento '" + request.getDocumento() + "' ya está registrado.");
        }

        Cliente cliente = new Cliente();
        cliente.setTipoDocumento(request.getTipoDocumento());
        cliente.setDocumento(request.getDocumento());
        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());

        Cliente savedCliente = clienteRepository.save(cliente);
        return toClienteResponse(savedCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse getClienteById(Integer id) {
        return clienteRepository.findById(id)
                .map(this::toClienteResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> getAllClientes(String documento, String nombre, Pageable pageable) {
        Specification<Cliente> spec = Specification.where(ClienteSpecification.documentoContains(documento))
                                                  .and(ClienteSpecification.nombreContains(nombre));
        
        return clienteRepository.findAll(spec, pageable).map(this::toClienteResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse getClienteByDocumento(String documento) {
        return clienteRepository.findByDocumento(documento)
                .map(this::toClienteResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con documento: " + documento));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> searchClientes(String query, Pageable pageable) {
        Specification<Cliente> spec = ClienteSpecification.searchByQuery(query);
        return clienteRepository.findAll(spec, pageable).map(this::toClienteResponse);
    }

    @Override
    @Transactional
    public ClienteResponse updateCliente(Integer id, UpdateClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        if (request.getNombre() != null) {
            cliente.setNombre(request.getNombre());
        }
        if (request.getEmail() != null) {
            cliente.setEmail(request.getEmail());
        }
        if (request.getTelefono() != null) {
            cliente.setTelefono(request.getTelefono());
        }

        Cliente updatedCliente = clienteRepository.save(cliente);
        return toClienteResponse(updatedCliente);
    }

    @Override
    @Transactional
    public void deleteCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteResponse toClienteResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getClienteId(),
                cliente.getTipoDocumento(),
                cliente.getDocumento(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getTelefono()
        );
    }
}
