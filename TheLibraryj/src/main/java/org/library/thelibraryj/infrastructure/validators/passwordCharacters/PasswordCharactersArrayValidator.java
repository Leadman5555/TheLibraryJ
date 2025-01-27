package org.library.thelibraryj.infrastructure.validators.passwordCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

class PasswordCharactersArrayValidator implements ConstraintValidator<ValidPasswordCharacters, char[]> {

    private static final Pattern ALLOWED_CHARS = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$");

    @Override
    public boolean isValid(char[] requestArray, ConstraintValidatorContext constraintValidatorContext) {
        return requestArray != null && ALLOWED_CHARS.matcher(new String(requestArray)).matches();
    }
}
