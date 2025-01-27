package org.library.thelibraryj.infrastructure.validators.usernameCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

class UsernameCharactersValidator implements ConstraintValidator<ValidUsernameCharacters, String> {

    private static final Pattern ALLOWED_CHARS = Pattern.compile("^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9_-]+$");

    @Override
    public boolean isValid(String requestString, ConstraintValidatorContext constraintValidatorContext) {
        return requestString != null && ALLOWED_CHARS.matcher(requestString).matches();
    }
}
