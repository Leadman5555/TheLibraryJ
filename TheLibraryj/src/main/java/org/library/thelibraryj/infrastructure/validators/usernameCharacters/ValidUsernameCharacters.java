package org.library.thelibraryj.infrastructure.validators.usernameCharacters;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = UsernameCharactersValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsernameCharacters {
    String message() default "Username contains invalid characters. Required: '^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9_-]+$'";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
