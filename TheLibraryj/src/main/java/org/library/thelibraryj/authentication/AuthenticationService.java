package org.library.thelibraryj.authentication;

import io.vavr.control.Either;
import jakarta.mail.MessagingException;
import org.library.thelibraryj.authentication.dto.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.AuthenticationResponse;
import org.library.thelibraryj.authentication.dto.RegisterRequest;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;

public interface AuthenticationService {
    Either<GeneralError, AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest);
    Either<GeneralError, Boolean> register(RegisterRequest registerRequest) throws MessagingException;
}
