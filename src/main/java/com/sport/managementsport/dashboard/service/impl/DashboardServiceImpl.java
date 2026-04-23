package com.sport.managementsport.dashboard.service.impl;

import com.sport.managementsport.booking.repository.ReservaRepository;
import com.sport.managementsport.common.enums.EstadoCancha;
import com.sport.managementsport.company.repository.CanchaRepository;
import com.sport.managementsport.dashboard.dto.DisponibilidadDiariaResponse;
import com.sport.managementsport.dashboard.dto.HorasOcupadasPorDia;
import com.sport.managementsport.dashboard.service.DashboardService;
import com.sport.managementsport.events.repository.MantenimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CanchaRepository canchaRepository;
    private final ReservaRepository reservaRepository;
    private final MantenimientoRepository mantenimientoRepository;

    private static final BigDecimal HORAS_OPERATIVAS_POR_DIA = new BigDecimal("17"); // 7am a 12am

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
}
