package org.library.thelibraryj.infrastructure.validators.usernameCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
class UsernameCharactersValidator implements ConstraintValidator<ValidUsernameCharacters, String> {

    private static final Pattern ALLOWED_CHARS = Pattern.compile("^[a-zA-Z0-9_-]+$");

    @Override
    public boolean isValid(String requestString, ConstraintValidatorContext constraintValidatorContext) {
        return ALLOWED_CHARS.matcher(requestString).matches();
    }
}
