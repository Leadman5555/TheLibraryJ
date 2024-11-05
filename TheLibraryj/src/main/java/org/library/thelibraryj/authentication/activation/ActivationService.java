package org.library.thelibraryj.authentication.activation;

import io.vavr.control.Either;
import org.library.thelibraryj.authentication.activation.dto.ActivationTokenResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;

import java.util.UUID;

public interface ActivationService {
    Either<GeneralError, ActivationTokenResponse> createActivationToken(UUID idForToken);
    ActivationTokenResponse createFirstActivationToken(UUID idForToken);
    Either<GeneralError, Boolean> useActivationToken(UUID token);
    void deleteAllUsedAndExpiredTokens();
}
