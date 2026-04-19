package com.sport.managementsport.booking.dto;

import com.sport.managementsport.common.enums.EstadoReserva;
import com.sport.managementsport.common.enums.TipoReserva;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponse {

    private Integer id;
    private Integer canchaId;
    private String nombreCancha; // Útil para mostrar en el frontend
    private Integer clienteId;
    private String nombreCliente; // Útil para mostrar en el frontend
    private Integer usuarioId;
    private String nombreUsuario; // Útil para mostrar en el frontend
    private Integer eventoId;
    private TipoReserva tipoReserva;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
    private EstadoReserva estadoReserva;
}
