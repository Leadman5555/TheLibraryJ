package org.library.thelibraryj.authentication.userAuth.domain;

interface UserAuthViewRepository {
    BasicUserAuthView getBasicUserAuthData(String email);

    LoginDataView getLoginData(String email);

    PasswordResetView getPasswordResetData(String email);

}
