package org.library.thelibraryj.authentication.googleAuth;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;

import java.io.IOException;

public interface GoogleAuthService {
    String getGoogleAuthUrl();
    Either<GeneralError, String> getGoogleAuthToken(String code) throws IOException;
}
