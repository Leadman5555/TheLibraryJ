package org.library.thelibraryj.infrastructure.validators.titleCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

class TitleCharactersValidator implements ConstraintValidator<ValidTitleCharacters, String> {

    private static final Pattern ALLOWED_CHARS = Pattern.compile("^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\\s'_\"!.-]*$");

    @Override
    public boolean isValid(String requestString, ConstraintValidatorContext constraintValidatorContext) {
        return requestString != null && ALLOWED_CHARS.matcher(requestString).matches();
    }
}
