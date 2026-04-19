package com.sport.managementsport.events.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateTimeRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateTimeRange {
    String message() default "La hora de fin debe ser posterior a la hora de inicio";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
