package com.sport.managementsport.finance.service.impl;

import com.sport.managementsport.booking.domain.Reserva;
import com.sport.managementsport.booking.service.ReservaService;
import com.sport.managementsport.common.enums.EstadoPago;
import com.sport.managementsport.common.enums.MetodoPago;
import com.sport.managementsport.common.enums.TipoTransaccion;
import com.sport.managementsport.dashboard.dto.KpiResponse;
import com.sport.managementsport.events.service.EventoService;
import com.sport.managementsport.exception.BusinessRuleException;
import com.sport.managementsport.exception.ResourceNotFoundException;
import com.sport.managementsport.finance.domain.Pago;
import com.sport.managementsport.finance.dto.AnularPagoRequest;
import com.sport.managementsport.finance.dto.CreatePagoRequest;
import com.sport.managementsport.finance.dto.PagoResponse;
import com.sport.managementsport.finance.repository.PagoRepository;
import com.sport.managementsport.finance.repository.PagoSpecification;
import com.sport.managementsport.finance.service.PagoService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaService reservaService;
    private final EventoService eventoService;

    public PagoServiceImpl(PagoRepository pagoRepository, @Lazy ReservaService reservaService, @Lazy EventoService eventoService) {
        this.pagoRepository = pagoRepository;
        this.reservaService = reservaService;
        this.eventoService = eventoService;
    }

    @Override
    @Transactional
    public PagoResponse createPago(CreatePagoRequest request) {
        Pago pago = new Pago();
        pago.setReserva(request.getReserva());
        pago.setEvento(request.getEvento());
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setTipoTransaccion(request.getTipoTransaccion());
        pago.setNota(request.getNota());
        pago.setFecha(LocalDate.now());

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

    @Override
    @Transactional(readOnly = true)
    public PagoResponse getPagoById(Integer id) {
        return pagoRepository.findById(id)
                .map(this::toPagoResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PagoResponse> getAllPagos(LocalDate desde, LocalDate hasta, MetodoPago metodo, Integer sucursalId, Pageable pageable) {
        Specification<Pago> spec = Specification.where(PagoSpecification.fechaBetween(desde, hasta))
                                                  .and(PagoSpecification.metodoPagoEquals(metodo))
                                                  .and(PagoSpecification.sucursalIdEquals(sucursalId));
        return pagoRepository.findAll(spec, pageable).map(this::toPagoResponse);
    }

    @Override
    @Transactional
    public void anularPago(Integer id, AnularPagoRequest request) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));

        if (pago.getEstado() == EstadoPago.ANULADO) {
            throw new BusinessRuleException("El pago ya ha sido anulado.");
        }

        pago.setEstado(EstadoPago.ANULADO);
        pago.setMotivoAnulacion(request.getMotivo());
        pagoRepository.save(pago);

        if (pago.getReserva() != null) {
            reservaService.revertirSaldosPorAnulacion(pago.getReserva().getReservaId(), pago.getMonto());
        } else if (pago.getEvento() != null) {
            eventoService.revertirSaldosPorAnulacion(pago.getEvento().getEventoId(), pago.getMonto());
        }
    }

    @Override
    @Transactional
    public void reasignarPagos(com.sport.managementsport.booking.domain.Reserva reservaOriginal, com.sport.managementsport.booking.domain.Reserva nuevaReserva) {
        List<Pago> pagosOriginales = pagoRepository.findByReservaReservaId(reservaOriginal.getReservaId());
        for (Pago pago : pagosOriginales) {
            pago.setReserva(nuevaReserva);
        }
        pagoRepository.saveAll(pagosOriginales);
    }

    @Override
    @Transactional(readOnly = true)
    public KpiResponse getIngresosAnuales(Integer sucursalId) {
        int currentYear = Year.now().getValue();
        BigDecimal total;
        if (sucursalId == null) {
            total = pagoRepository.sumByTipoTransaccionAndYear(TipoTransaccion.INGRESO, currentYear);
        } else {
            total = pagoRepository.sumByTipoTransaccionAndYearAndSucursal(TipoTransaccion.INGRESO, currentYear, sucursalId);
        }
        return new KpiResponse(total);
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
