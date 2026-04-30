package com.sport.managementsport.booking.service.impl;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.booking.repository.ReservaRepository;
import com.sport.managementsport.booking.service.ReservaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    @Transactional
    public Reserva createReserva(Reserva reserva) {
        // TODO: Añadir lógica de negocio:
        // 1. Validar disponibilidad de la cancha en la fecha y hora.
        // 2. Calcular monto_total basado en el precio de la cancha.
        // 3. Calcular saldo_pendiente.
        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> getReservaById(Integer id) {
        return reservaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }

    @Override
    @Transactional
    public Reserva updateReserva(Integer id, Reserva reservaDetails) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));

        // Actualizar solo campos permitidos. La lógica de negocio aquí puede ser compleja.
        reserva.setFecha(reservaDetails.getFecha());
        reserva.setHoraInicio(reservaDetails.getHoraInicio());
        reserva.setHoraFin(reservaDetails.getHoraFin());
        reserva.setEstadoReserva(reservaDetails.getEstadoReserva());
        reserva.setMontoPagado(reservaDetails.getMontoPagado());
        reserva.setSaldoPendiente(reserva.getMontoTotal().subtract(reservaDetails.getMontoPagado()));

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public void deleteReserva(Integer id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada con id: " + id);
        }
        reservaRepository.deleteById(id);
    }
}
