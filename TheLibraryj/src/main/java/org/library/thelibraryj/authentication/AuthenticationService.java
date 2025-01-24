package org.library.thelibraryj.authentication;

import io.vavr.control.Either;
import jakarta.servlet.http.Cookie;
import org.library.thelibraryj.authentication.dto.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.AuthenticationResponse;
import org.library.thelibraryj.authentication.dto.RegisterRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;

public interface AuthenticationService {
    Either<GeneralError, AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest);
    Either<GeneralError, UserCreationResponse> register(RegisterRequest registerRequest);
    Either<GeneralError, Boolean> resendActivationEmail(String email);
    Cookie clearRefreshToken();
    String regenerateAccessToken(Cookie[] cookies);
}
