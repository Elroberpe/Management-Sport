package com.sport.managementsport.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsuarioRequest {

    @Size(max = 50, message = "Si se proporciona, el nombre de usuario no puede exceder los 50 caracteres")
    private String username;

    @Size(max = 150, message = "Si se proporciona, el nombre no puede exceder los 150 caracteres")
    private String nombre;

    @Email(message = "Si se proporciona, el formato del email es inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;
}
