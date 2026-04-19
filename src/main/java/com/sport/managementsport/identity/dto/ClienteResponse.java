package com.sport.managementsport.identity.dto;

import com.sport.managementsport.common.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Integer id;
    private TipoDocumento tipoDocumento;
    private String documento;
    private String nombre;
    private String email;
    private String telefono;
}
