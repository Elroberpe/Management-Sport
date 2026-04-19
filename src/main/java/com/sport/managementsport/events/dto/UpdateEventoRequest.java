package com.sport.managementsport.events.dto;

import com.sport.managementsport.common.enums.TipoEvento;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventoRequest {

    @Size(max = 150)
    private String nombre;

    @Size(max = 4000)
    private String descripcion;

    private TipoEvento tipoEvento;

    @Positive(message = "El monto pactado debe ser un valor positivo")
    private BigDecimal montoPactado;

    @FutureOrPresent
    private LocalDate fechaInicio;

    @FutureOrPresent
    private LocalDate fechaFin;
}
