package org.library.thelibraryj.infrastructure.validators.titleCharacters;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
class TitleCharactersValidator implements ConstraintValidator<ValidTitleCharacters, String> {

    private static final Pattern ALLOWED_CHARS = Pattern.compile("^[a-zA-Z\\s']*$");

    @Override
    public boolean isValid(String requestString, ConstraintValidatorContext constraintValidatorContext) {
        return ALLOWED_CHARS.matcher(requestString).matches();
    }
}
