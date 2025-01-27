package org.library.thelibraryj.infrastructure.validators.titleCharacters;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TitleCharactersValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTitleCharacters {
    String message() default "Title contains invalid characters. Required: '^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\\s'_\"!.-]*$";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
