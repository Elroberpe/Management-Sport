package com.sport.managementsport.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SedeStatusResponse {
    private Integer sucursalId;
    private String nombreSede;
    private long canchasTotales;
    private long canchasActivas;
}
