package com.sport.managementsport.company.dto;

import com.sport.managementsport.common.enums.EstadoCancha;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanchaResponse {

    private Integer id;
    private Integer sucursalId;
    private String nombre;
    private EstadoCancha estadoCancha;
    private BigDecimal precioHora;
}
