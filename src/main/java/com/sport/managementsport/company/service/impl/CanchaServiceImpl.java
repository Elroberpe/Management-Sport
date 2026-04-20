package com.sport.managementsport.company.service.impl;

import com.sport.managementsport.common.enums.EstadoCancha;
import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.company.domain.Sucursal;
import com.sport.managementsport.company.dto.CanchaResponse;
import com.sport.managementsport.company.dto.CreateCanchaRequest;
import com.sport.managementsport.company.dto.UpdateCanchaRequest;
import com.sport.managementsport.company.dto.UpdateEstadoCanchaRequest;
import com.sport.managementsport.company.repository.CanchaRepository;
import com.sport.managementsport.company.repository.CanchaSpecification;
import com.sport.managementsport.company.service.CanchaService;
import com.sport.managementsport.company.service.SucursalService;
import com.sport.managementsport.exception.DuplicateResourceException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CanchaServiceImpl implements CanchaService {

    private final CanchaRepository canchaRepository;
    private final SucursalService sucursalService;

    @Override
    @Transactional
    public CanchaResponse createCancha(CreateCanchaRequest request) {
        Sucursal sucursal = sucursalService.findSucursalEntityById(request.getSucursalId());

        if (canchaRepository.existsByNombreAndSucursalSucursalId(request.getNombre(), request.getSucursalId())) {
            throw new DuplicateResourceException("Ya existe una cancha con el nombre '" + request.getNombre() + "' en esta sucursal.");
        }

        Cancha cancha = new Cancha();
        cancha.setSucursal(sucursal);
        cancha.setNombre(request.getNombre());
        cancha.setPrecioHora(request.getPrecioHora());

        Cancha savedCancha = canchaRepository.save(cancha);
        return toCanchaResponse(savedCancha);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CanchaResponse> getCanchaById(Integer id) {
        return canchaRepository.findById(id).map(this::toCanchaResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CanchaResponse> getAllCanchas(Integer sucursalId, EstadoCancha estado) {
        Specification<Cancha> spec = Specification.where(CanchaSpecification.sucursalIdEquals(sucursalId))
                                                  .and(CanchaSpecification.estadoEquals(estado));
        
        return canchaRepository.findAll(spec).stream()
                .map(this::toCanchaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CanchaResponse updateCancha(Integer id, UpdateCanchaRequest request) {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha no encontrada con id: " + id));

        if (request.getNombre() != null) {
            cancha.setNombre(request.getNombre());
        }
        if (request.getPrecioHora() != null) {
            cancha.setPrecioHora(request.getPrecioHora());
        }

        Cancha updatedCancha = canchaRepository.save(cancha);
        return toCanchaResponse(updatedCancha);
    }

    @Override
    @Transactional
    public CanchaResponse updateEstadoCancha(Integer id, UpdateEstadoCanchaRequest request) {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha no encontrada con id: " + id));
        
        cancha.setEstadoCancha(request.getEstadoCancha());
        Cancha updatedCancha = canchaRepository.save(cancha);
        return toCanchaResponse(updatedCancha);
    }

    @Override
    @Transactional
    public void deleteCancha(Integer id) {
        if (!canchaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cancha no encontrada con id: " + id);
        }
        canchaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CanchaResponse> getCanchasBySucursalId(Integer sucursalId) {
        if (!sucursalService.sucursalExists(sucursalId)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con id: " + sucursalId);
        }
        return canchaRepository.findBySucursalSucursalId(sucursalId).stream()
                .map(this::toCanchaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Cancha findCanchaEntityById(Integer id) {
        return canchaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha no encontrada con id: " + id));
    }

    @Override
    public boolean canchaExists(Integer id) {
        return canchaRepository.existsById(id);
    }

    @Override
    public boolean hasCanchasInSucursal(Integer sucursalId) {
        return canchaRepository.existsBySucursalSucursalId(sucursalId);
    }

    private CanchaResponse toCanchaResponse(Cancha cancha) {
        return new CanchaResponse(
                cancha.getCanchaId(),
                cancha.getSucursal().getSucursalId(),
                cancha.getNombre(),
                cancha.getEstadoCancha(),
                cancha.getPrecioHora()
        );
    }
}
