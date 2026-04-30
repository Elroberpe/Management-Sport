package com.sport.managementsport.finance.service;

import com.sport.managementsport.finance.domain.Pago;
import java.util.List;
import java.util.Optional;

public interface PagoService {

    Pago createPago(Pago pago);

    Optional<Pago> getPagoById(Integer id);

    List<Pago> getAllPagos();

    Pago updatePago(Integer id, Pago pagoDetails);

    void deletePago(Integer id);
}
