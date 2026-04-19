package com.sport.managementsport.booking.validation;

import com.sport.managementsport.booking.dto.CreateReservaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, CreateReservaRequest> {

    @Override
    public boolean isValid(CreateReservaRequest request, ConstraintValidatorContext context) {
        if (request.getHoraInicio() == null || request.getHoraFin() == null) {
            return true; // Se deja a @NotNull manejar esto
        }
        return request.getHoraFin().isAfter(request.getHoraInicio());
    }
}
