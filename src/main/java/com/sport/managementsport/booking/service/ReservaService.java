package com.sport.managementsport.booking.service;

import com.sport.managementsport.booking.domain.Reserva;
import java.util.List;
import java.util.Optional;

public interface ReservaService {

    Reserva createReserva(Reserva reserva);

    Optional<Reserva> getReservaById(Integer id);

    List<Reserva> getAllReservas();

    Reserva updateReserva(Integer id, Reserva reservaDetails);

    void deleteReserva(Integer id);
}
