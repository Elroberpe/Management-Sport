package com.sport.managementsport.identity.service.impl;

import com.sport.managementsport.identity.domain.Cliente;
import com.sport.managementsport.identity.repository.ClienteRepository;
import com.sport.managementsport.identity.service.ClienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional
    public Cliente createCliente(Cliente cliente) {
        // Aquí se podrían añadir validaciones, como la unicidad del documento
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> getClienteById(Integer id) {
        return clienteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional
    public Cliente updateCliente(Integer id, Cliente clienteDetails) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));

        cliente.setTipoDocumento(clienteDetails.getTipoDocumento());
        cliente.setDocumento(clienteDetails.getDocumento());
        cliente.setNombre(clienteDetails.getNombre());
        cliente.setEmail(clienteDetails.getEmail());
        cliente.setTelefono(clienteDetails.getTelefono());

        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void deleteCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
