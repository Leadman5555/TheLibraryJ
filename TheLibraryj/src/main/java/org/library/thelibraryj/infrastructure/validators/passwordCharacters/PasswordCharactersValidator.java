package org.library.thelibraryj.infrastructure.validators.passwordCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

class PasswordCharactersValidator implements ConstraintValidator<ValidPasswordCharacters, String> {

    private static final Pattern ALLOWED_CHARS = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$");

    @Override
    public boolean isValid(String requestString, ConstraintValidatorContext constraintValidatorContext) {
        return requestString != null && ALLOWED_CHARS.matcher(requestString).matches();
    }
}
