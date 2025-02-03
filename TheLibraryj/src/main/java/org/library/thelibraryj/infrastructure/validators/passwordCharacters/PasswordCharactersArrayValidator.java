package org.library.thelibraryj.infrastructure.validators.passwordCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class PasswordCharactersArrayValidator implements ConstraintValidator<ValidPasswordCharacters, char[]> {

    @Override
    public boolean isValid(char[] requestArray, ConstraintValidatorContext constraintValidatorContext) {
        return requestArray != null && PasswordCharactersMatcher.matches(new String(requestArray));
    }
}
