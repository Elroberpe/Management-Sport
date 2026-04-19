package com.sport.managementsport.identity.dto;

import com.sport.managementsport.common.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUsuarioRequest {

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Integer empresaId;

    private Integer sucursalId; // Opcional en el DTO, se valida en la capa de servicio

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario no puede exceder los 50 caracteres")
    private String username;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email es inválido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario rol;
}
