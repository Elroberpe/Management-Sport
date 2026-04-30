package com.sport.managementsport.finance.service.impl;

import com.sport.managementsport.finance.domain.Pago;
import com.sport.managementsport.finance.repository.PagoRepository;
import com.sport.managementsport.finance.service.PagoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;

    public PagoServiceImpl(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    @Transactional
    public Pago createPago(Pago pago) {
        // TODO: Añadir lógica de negocio:
        // 1. Validar que la reserva o el evento existan.
        // 2. Actualizar el saldo de la reserva/evento asociado.
        return pagoRepository.save(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pago> getPagoById(Integer id) {
        return pagoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> getAllPagos() {
        return pagoRepository.findAll();
    }

    @Override
    @Transactional
    public Pago updatePago(Integer id, Pago pagoDetails) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con id: " + id));

        // La lógica de actualización de un pago puede ser compleja (ej. solo anular)
        pago.setFecha(pagoDetails.getFecha());
        pago.setMonto(pagoDetails.getMonto());
        pago.setMetodoPago(pagoDetails.getMetodoPago());
        pago.setEstado(pagoDetails.getEstado());
        pago.setReferencia(pagoDetails.getReferencia());
        pago.setNota(pagoDetails.getNota());

        return pagoRepository.save(pago);
    }

    @Override
    @Transactional
    public void deletePago(Integer id) {
        // En un sistema real, los pagos no se borran, se anulan.
        // Esto es solo para el CRUD básico.
        if (!pagoRepository.existsById(id)) {
            throw new RuntimeException("Pago no encontrado con id: " + id);
        }
        pagoRepository.deleteById(id);
    }
}
