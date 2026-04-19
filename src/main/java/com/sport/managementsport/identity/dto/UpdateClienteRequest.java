package com.sport.managementsport.identity.dto;

import jakarta.validation.constraints.Email;
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
public class UpdateClienteRequest {

    @Size(max = 100, message = "Si se proporciona, el nombre no puede exceder los 100 caracteres")
    private String nombre;

    @Email(message = "Si se proporciona, el formato del email es inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    @Pattern(regexp = "^[0-9]{9,12}$", message = "Si se proporciona, el teléfono debe contener solo números y tener entre 9 y 12 dígitos")
    private String telefono;
}
