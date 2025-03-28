package org.library.thelibraryj.authentication;

import io.vavr.control.Either;
import jakarta.servlet.http.Cookie;
import org.library.thelibraryj.authentication.dto.request.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.response.AuthenticationResponse;
import org.library.thelibraryj.authentication.dto.request.RegisterRequest;
import org.library.thelibraryj.authentication.userAuth.dto.response.UserCreationResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.lang.Nullable;

public interface AuthenticationService {
    Either<GeneralError, AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest);
    Either<GeneralError, UserCreationResponse> register(RegisterRequest registerRequest);
    Either<GeneralError, Boolean> resendActivationEmail(String email);
    Cookie clearRefreshToken();
    String regenerateAccessToken(@Nullable Cookie[] cookies);
}
