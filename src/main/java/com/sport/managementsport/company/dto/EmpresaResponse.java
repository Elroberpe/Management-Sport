package com.sport.managementsport.company.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponse {

    private Long id;
    private String nombreComercial;
    private String razonSocial;
    private String logoUrl;
    private String emailContacto;
    private String telefonoPrincipal;

}
