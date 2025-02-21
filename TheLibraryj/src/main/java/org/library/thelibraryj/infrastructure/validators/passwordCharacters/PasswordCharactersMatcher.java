package org.library.thelibraryj.infrastructure.validators.passwordCharacters;

import java.util.regex.Pattern;

class PasswordCharactersMatcher {
    private static final Pattern ALLOWED_CHARS = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$");

    public static boolean matches(String toMatch){
       return  ALLOWED_CHARS.matcher(toMatch).matches();
    }
}
