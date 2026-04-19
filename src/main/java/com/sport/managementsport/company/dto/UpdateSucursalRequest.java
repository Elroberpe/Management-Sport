package com.sport.managementsport.company.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSucursalRequest {

    @Size(max = 100, message = "Si se proporciona, el nombre no puede exceder los 100 caracteres")
    private String nombre;

    @Size(max = 250, message = "Si se proporciona, la dirección no puede exceder los 250 caracteres")
    private String direccion;

    @Pattern(regexp = "^[0-9]{0,12}$", message = "Si se proporciona, el teléfono debe contener solo números y tener un máximo de 12 dígitos")
    private String telefono;

    private Boolean activo;
}
