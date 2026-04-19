package com.sport.managementsport.company.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SucursalResponse {
    private Integer id;
    private Integer empresaId;
    private String nombre;
    private String direccion;
    private String telefono;
    private boolean activo;
}
