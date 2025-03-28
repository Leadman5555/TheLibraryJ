package org.library.thelibraryj.infrastructure.validators.passwordCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class PasswordCharactersValidator implements ConstraintValidator<ValidPasswordCharacters, String> {

    @Override
    public boolean isValid(String requestString, ConstraintValidatorContext constraintValidatorContext) {
        return requestString != null && PasswordCharactersMatcher.matches(requestString);
    }
}
