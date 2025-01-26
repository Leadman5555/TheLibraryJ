package org.library.thelibraryj.infrastructure.validators.passwordCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
class PasswordCharactersValidator implements ConstraintValidator<ValidPasswordCharacters, String> {

    private static final Pattern ALLOWED_CHARS = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$");

    @Override
    public boolean isValid(String requestString, ConstraintValidatorContext constraintValidatorContext) {
        return ALLOWED_CHARS.matcher(requestString).matches();
    }
}
