package org.library.thelibraryj.infrastructure.validators.fileValidators.textFile;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {TextFileFormatValidator.class, TextFileArrayFormatValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTextFileFormat {
    String message() default "File format not supported. Required: .doc, .docx, .txt, .odt";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}