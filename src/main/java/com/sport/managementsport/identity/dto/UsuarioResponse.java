package com.sport.managementsport.identity.dto;

import com.sport.managementsport.common.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Integer id;
    private Integer empresaId;
    private Integer sucursalId;
    private String username;
    private String nombre;
    private String email;
    private RolUsuario rol;
    private LocalDateTime ultimoLogin;
}
