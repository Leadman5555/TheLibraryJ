package org.library.thelibraryj.authentication;

import java.util.Arrays;

public interface PasswordControl {
    default void zeroPassword(char[] passwordToZero){
        Arrays.fill(passwordToZero, '0');
    }
}
