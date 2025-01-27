package org.library.thelibraryj.infrastructure.validators.batchSize;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = BatchSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBatchSize {
    String message() default "Up to 50 elements allowed per request.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
