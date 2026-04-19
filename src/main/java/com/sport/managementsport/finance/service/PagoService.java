package com.sport.managementsport.finance.service;

import com.sport.managementsport.finance.dto.CreatePagoRequest;
import com.sport.managementsport.finance.dto.PagoResponse;

import java.util.List;

public interface PagoService {
    PagoResponse createPago(CreatePagoRequest request);
    List<PagoResponse> getPagosByReservaId(Integer reservaId);
}
