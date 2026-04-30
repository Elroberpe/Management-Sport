package com.sport.managementsport.company.service.impl;

import com.sport.managementsport.company.domain.Cancha;
import com.sport.managementsport.company.repository.CanchaRepository;
import com.sport.managementsport.company.service.CanchaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CanchaServiceImpl implements CanchaService {

    private final CanchaRepository canchaRepository;

    public CanchaServiceImpl(CanchaRepository canchaRepository) {
        this.canchaRepository = canchaRepository;
    }

    @Override
    @Transactional
    public Cancha createCancha(Cancha cancha) {
        // Aquí se podrían añadir validaciones, como verificar que la sucursal exista
        return canchaRepository.save(cancha);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cancha> getCanchaById(Integer id) {
        return canchaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cancha> getAllCanchas() {
        return canchaRepository.findAll();
    }

    @Override
    @Transactional
    public Cancha updateCancha(Integer id, Cancha canchaDetails) {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada con id: " + id));

        cancha.setEstadoCancha(canchaDetails.getEstadoCancha());
        cancha.setPrecioHora(canchaDetails.getPrecioHora());
        // La sucursal (sucursal_id) no debería cambiarse en una actualización simple

        return canchaRepository.save(cancha);
    }

    @Override
    @Transactional
    public void deleteCancha(Integer id) {
        if (!canchaRepository.existsById(id)) {
            throw new RuntimeException("Cancha no encontrada con id: " + id);
        }
        canchaRepository.deleteById(id);
    }
}
