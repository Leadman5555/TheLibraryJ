package org.library.thelibraryj.infrastructure.validators.passwordCharacters;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {PasswordCharactersValidator.class, PasswordCharactersArrayValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPasswordCharacters {
    String message() default "Password does not meet requirements. Required: '^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
