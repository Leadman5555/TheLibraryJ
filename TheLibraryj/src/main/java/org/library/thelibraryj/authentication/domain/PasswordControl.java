package org.library.thelibraryj.authentication.domain;

import java.util.Arrays;

sealed interface PasswordControl permits AuthenticationServiceImpl {
    default void zeroPassword(char[] passwordToZero) {
        Arrays.fill(passwordToZero, '0');
    }
}
