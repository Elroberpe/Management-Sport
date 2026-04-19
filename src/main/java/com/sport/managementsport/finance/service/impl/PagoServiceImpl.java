package com.sport.managementsport.finance.service.impl;

import com.sport.managementsport.finance.domain.Pago;
import com.sport.managementsport.finance.dto.CreatePagoRequest;
import com.sport.managementsport.finance.dto.PagoResponse;
import com.sport.managementsport.finance.repository.PagoRepository;
import com.sport.managementsport.finance.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;

    @Override
    @Transactional
    public PagoResponse createPago(CreatePagoRequest request) {
        Pago pago = new Pago();
        pago.setReserva(request.getReserva());
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setTipoTransaccion(request.getTipoTransaccion());
        pago.setNota(request.getNota());
        pago.setFecha(LocalDate.now());
        // El estado por defecto es COMPLETADO

        Pago savedPago = pagoRepository.save(pago);
        return toPagoResponse(savedPago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> getPagosByReservaId(Integer reservaId) {
        return pagoRepository.findByReservaReservaId(reservaId).stream()
                .map(this::toPagoResponse)
                .collect(Collectors.toList());
    }

    private PagoResponse toPagoResponse(Pago pago) {
        return PagoResponse.builder()
                .id(pago.getPagoId())
                .reservaId(pago.getReserva() != null ? pago.getReserva().getReservaId() : null)
                .eventoId(pago.getEvento() != null ? pago.getEvento().getEventoId() : null)
                .fecha(pago.getFecha())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estado(pago.getEstado())
                .tipoTransaccion(pago.getTipoTransaccion())
                .referencia(pago.getReferencia())
                .nota(pago.getNota())
                .build();
    }
}
