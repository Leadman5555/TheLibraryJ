package org.library.thelibraryj.authentication;

import java.util.Arrays;

interface PasswordControl {
    static void zeroPassword(char[] passwordToZero){
        Arrays.fill(passwordToZero, '0');
    }
}
