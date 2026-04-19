package com.sport.managementsport.events.dto;

import com.sport.managementsport.common.enums.EstadoMantenimiento;
import com.sport.managementsport.common.enums.TipoMantenimiento;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MantenimientoResponse {
    private Integer id;
    private Integer canchaId;
    private String nombreCancha;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private TipoMantenimiento tipoMantenimiento;
    private EstadoMantenimiento estadoMantenimiento;
    private String motivo;
}
