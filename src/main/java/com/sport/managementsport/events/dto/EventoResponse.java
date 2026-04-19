package com.sport.managementsport.events.dto;

import com.sport.managementsport.common.enums.EstadoEvento;
import com.sport.managementsport.common.enums.TipoEvento;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class EventoResponse {
    private Integer id;
    private Integer sucursalId;
    private Integer clienteId;
    private String nombreCliente;
    private String nombre;
    private String descripcion;
    private TipoEvento tipoEvento;
    private BigDecimal montoPactado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoEvento estado;
    private List<ReservaInfo> reservasAsociadas;

    @Getter
    @Builder
    public static class ReservaInfo {
        private Integer reservaId;
        private Integer canchaId;
        private String nombreCancha;
        private LocalDate fecha;
        private String horario; // e.g., "18:00 - 22:00"
    }
}
