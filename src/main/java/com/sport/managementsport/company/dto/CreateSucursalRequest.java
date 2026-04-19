package com.sport.managementsport.company.dto;

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
public class CreateSucursalRequest {

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Integer empresaId;

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 250, message = "La dirección no puede exceder los 250 caracteres")
    private String direccion;

    @Size(max = 12, message = "El teléfono no puede exceder los 12 caracteres")
    private String telefono;
}
