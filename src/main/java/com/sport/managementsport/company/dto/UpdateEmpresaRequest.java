package com.sport.managementsport.company.dto;

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
public class UpdateEmpresaRequest {

    @Size(min = 3, max = 100, message = "Si se proporciona, el nombre comercial debe tener entre 3 y 100 caracteres")
    private String nombreComercial;

    @Email(message = "El formato del email de contacto debe ser inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String emailContacto;

    @Pattern(regexp = "^[0-9]{9,12}$", message = "El teléfono debe contener solo números y tener entre 9 y 12 dígitos")
    private String telefonoPrincipal;

    private String logoUrl;
}
