package com.sport.managementsport.dashboard.service.impl;

import com.sport.managementsport.booking.repository.ReservaRepository;
import com.sport.managementsport.common.enums.EstadoCancha;
import com.sport.managementsport.common.enums.EstadoReserva;
import com.sport.managementsport.common.enums.TipoTransaccion;
import com.sport.managementsport.company.repository.CanchaRepository;
import com.sport.managementsport.dashboard.dto.ActividadDiariaResponse;
import com.sport.managementsport.dashboard.dto.ActividadPorDia;
import com.sport.managementsport.dashboard.dto.DisponibilidadDiariaResponse;
import com.sport.managementsport.dashboard.dto.HorasOcupadasPorDia;
import com.sport.managementsport.dashboard.dto.KpiResponse;
import com.sport.managementsport.dashboard.service.DashboardService;
import com.sport.managementsport.events.repository.MantenimientoRepository;
import com.sport.managementsport.finance.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CanchaRepository canchaRepository;
    private final ReservaRepository reservaRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final PagoRepository pagoRepository;

    private static final BigDecimal HORAS_OPERATIVAS_POR_DIA = new BigDecimal("17");
    private static final int DIAS_PERIODO_OCUPACION = 30;
    private static final List<String> ESTADOS_RESERVA_ACTIVIDAD = Stream.of(EstadoReserva.PENDIENTE, EstadoReserva.PAGADA, EstadoReserva.COMPLETADO)
            .map(Enum::name)
            .collect(Collectors.toList());

    @Override
    public List<DisponibilidadDiariaResponse> getDisponibilidadSemanal(Integer sucursalId, LocalDate fechaBase) {
        LocalDate lunes = fechaBase.with(DayOfWeek.MONDAY);
        LocalDate domingo = fechaBase.with(DayOfWeek.SUNDAY);

        long numCanchas = canchaRepository.countBySucursal_SucursalIdAndEstadoCanchaNot(sucursalId, EstadoCancha.INACTIVA);
        if (numCanchas == 0) {
            return new ArrayList<>();
        }

        BigDecimal horasTotalesPorDia = HORAS_OPERATIVAS_POR_DIA.multiply(BigDecimal.valueOf(numCanchas));

        List<HorasOcupadasPorDia> horasOcupadasReservas = reservaRepository.findHorasOcupadasPorSucursalYFechas(sucursalId, lunes, domingo);
        List<HorasOcupadasPorDia> horasOcupadasMantenimientos = mantenimientoRepository.findHorasOcupadasPorSucursalYFechas(sucursalId, lunes, domingo);

        Map<LocalDate, BigDecimal> mapaHorasOcupadas = horasOcupadasReservas.stream()
                .collect(Collectors.toMap(HorasOcupadasPorDia::getDia, HorasOcupadasPorDia::getHorasOcupadas));

        horasOcupadasMantenimientos.forEach(m -> mapaHorasOcupadas.merge(m.getDia(), m.getHorasOcupadas(), BigDecimal::add));

        List<DisponibilidadDiariaResponse> disponibilidadSemanal = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate diaActual = lunes.plusDays(i);
            BigDecimal horasOcupadas = mapaHorasOcupadas.getOrDefault(diaActual, BigDecimal.ZERO);
            BigDecimal horasDisponibles = horasTotalesPorDia.subtract(horasOcupadas);

            String nombreDia = diaActual.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase();
            disponibilidadSemanal.add(new DisponibilidadDiariaResponse(nombreDia, diaActual, horasDisponibles));
        }

        return disponibilidadSemanal;
    }

    @Override
    public KpiResponse getReservasCompletadasHoy(Integer sucursalId) {
        long total;
        if (sucursalId == null) {
            total = reservaRepository.countByEstadoReservaAndFecha(EstadoReserva.COMPLETADO, LocalDate.now());
        } else {
            total = reservaRepository.countBySucursalAndEstadoAndFecha(sucursalId, EstadoReserva.COMPLETADO, LocalDate.now());
        }
        return new KpiResponse(total);
    }

    @Override
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

    @Override
    public KpiResponse getTasaOcupacionMensual(Integer sucursalId) {
        LocalDate fechaHasta = LocalDate.now();
        LocalDate fechaDesde = fechaHasta.minusDays(DIAS_PERIODO_OCUPACION);

        long numCanchas;
        BigDecimal horasOcupadasReservas;
        BigDecimal horasOcupadasMantenimientos;

        if (sucursalId == null) {
            numCanchas = canchaRepository.countByEstadoCanchaNot(EstadoCancha.INACTIVA);
            horasOcupadasReservas = reservaRepository.sumHorasOcupadasByFechas(fechaDesde, fechaHasta);
            horasOcupadasMantenimientos = mantenimientoRepository.sumHorasOcupadasByFechas(fechaDesde, fechaHasta);
        } else {
            numCanchas = canchaRepository.countBySucursal_SucursalIdAndEstadoCanchaNot(sucursalId, EstadoCancha.INACTIVA);
            horasOcupadasReservas = reservaRepository.findHorasOcupadasPorSucursalYFechas(sucursalId, fechaDesde, fechaHasta)
                                                     .stream().map(HorasOcupadasPorDia::getHorasOcupadas).reduce(BigDecimal.ZERO, BigDecimal::add);
            horasOcupadasMantenimientos = mantenimientoRepository.findHorasOcupadasPorSucursalYFechas(sucursalId, fechaDesde, fechaHasta)
                                                                .stream().map(HorasOcupadasPorDia::getHorasOcupadas).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (numCanchas == 0) {
            return new KpiResponse(BigDecimal.ZERO);
        }

        BigDecimal horasTotalesOcupadas = horasOcupadasReservas.add(horasOcupadasMantenimientos);
        BigDecimal horasTotalesDisponibles = HORAS_OPERATIVAS_POR_DIA
                .multiply(BigDecimal.valueOf(numCanchas))
                .multiply(BigDecimal.valueOf(DIAS_PERIODO_OCUPACION));

        if (horasTotalesDisponibles.compareTo(BigDecimal.ZERO) == 0) {
            return new KpiResponse(BigDecimal.ZERO);
        }

        BigDecimal tasaOcupacion = horasTotalesOcupadas.divide(horasTotalesDisponibles, 4, RoundingMode.HALF_UP)
                                                       .multiply(new BigDecimal("100"));

        return new KpiResponse(tasaOcupacion.setScale(2, RoundingMode.HALF_UP));
    }

    @Override
    public List<ActividadDiariaResponse> getActividadReservas(Integer sucursalId, String periodo) {
        LocalDate fechaHasta = LocalDate.now();
        LocalDate fechaDesde;

        if ("SEMANA".equalsIgnoreCase(periodo)) {
            fechaDesde = fechaHasta.with(DayOfWeek.MONDAY);
        } else { // MES por defecto
            fechaDesde = fechaHasta.withDayOfMonth(1);
        }

        List<ActividadPorDia> actividad;
        if (sucursalId == null) {
            actividad = reservaRepository.countActividadPorDia(fechaDesde, fechaHasta, ESTADOS_RESERVA_ACTIVIDAD);
        } else {
            actividad = reservaRepository.countActividadPorDiaYPorSucursal(sucursalId, fechaDesde, fechaHasta, ESTADOS_RESERVA_ACTIVIDAD);
        }

        Map<LocalDate, Long> mapaActividad = actividad.stream()
                .collect(Collectors.toMap(ActividadPorDia::getFecha, ActividadPorDia::getCantidad));

        List<ActividadDiariaResponse> resultado = new ArrayList<>();
        for (LocalDate fecha = fechaDesde; !fecha.isAfter(fechaHasta); fecha = fecha.plusDays(1)) {
            long cantidad = mapaActividad.getOrDefault(fecha, 0L);
            resultado.add(new ActividadDiariaResponse(fecha, cantidad));
        }

        return resultado;
    }
}
