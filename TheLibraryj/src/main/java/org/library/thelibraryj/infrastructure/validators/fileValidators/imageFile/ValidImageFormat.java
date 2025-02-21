package org.library.thelibraryj.infrastructure.validators.fileValidators.imageFile;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ImageFormatValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageFormat {
    String message() default "File format not supported. Required: .jpg, .jpeg, .png, .webp";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}