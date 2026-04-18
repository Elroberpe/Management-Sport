package com.sport.managementsport.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class CreateEmpresaRequest {

    @NotBlank(message = "El nombre comercial es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre comercial debe tener entre 3 y 100 caracteres")
    private String nombreComercial;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(min = 3, max = 100, message = "La razón social debe tener entre 3 y 100 caracteres")
    private String razonSocial;

    @NotBlank(message = "El email de contacto es obligatorio")
    @Email(message = "El formato del email de contacto es inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String emailContacto;

    @NotBlank(message = "El teléfono principal es obligatorio")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "El teléfono debe contener solo números y tener entre 9 y 12 dígitos")
    private String telefonoPrincipal;

    private String logoUrl; // Opcional
}
