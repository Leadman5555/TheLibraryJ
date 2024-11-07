package org.library.thelibraryj.authentication.tokenServices;

import io.vavr.control.Either;
import org.library.thelibraryj.authentication.tokenServices.dto.activation.ActivationTokenResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;

import java.util.UUID;

public interface ActivationService {
    Either<GeneralError, ActivationTokenResponse> createActivationToken(String forEmail);
    ActivationTokenResponse createFirstActivationToken(UUID idForToken);
    Either<GeneralError, Boolean> consumeActivationToken(UUID token);
    void deleteAllUsedAndExpiredTokens();
}
