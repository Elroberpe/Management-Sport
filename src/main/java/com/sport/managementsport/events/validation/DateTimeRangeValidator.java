package com.sport.managementsport.events.validation;

import com.sport.managementsport.events.dto.CreateMantenimientoRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateTimeRangeValidator implements ConstraintValidator<ValidDateTimeRange, CreateMantenimientoRequest> {

    @Override
    public boolean isValid(CreateMantenimientoRequest request, ConstraintValidatorContext context) {
        if (request.getHoraInicio() == null || request.getHoraFin() == null) {
            return true; // Se deja a @NotNull manejar esto
        }
        return request.getHoraFin().isAfter(request.getHoraInicio());
    }
}
