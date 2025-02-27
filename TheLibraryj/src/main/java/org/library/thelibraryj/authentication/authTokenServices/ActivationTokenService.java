package org.library.thelibraryj.authentication.authTokenServices;

import io.vavr.control.Either;
import org.library.thelibraryj.authentication.authTokenServices.dto.activation.ActivationTokenResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.tokenServices.TokenService;

import java.util.UUID;

public interface ActivationTokenService extends TokenService {
    Either<GeneralError, ActivationTokenResponse> createActivationToken(String forEmail);
    ActivationTokenResponse createFirstActivationToken(UUID idForToken);
    Either<GeneralError, Boolean> consumeActivationToken(UUID token);
}
