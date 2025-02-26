package org.library.thelibraryj.authentication.authTokenServices;

import io.vavr.control.Either;
import org.library.thelibraryj.authentication.authTokenServices.dto.password.PasswordResetRequest;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.tokenServices.TokenService;

public interface PasswordResetTokenService extends TokenService {
    Either<GeneralError, Boolean> startPasswordResetProcedure(String email);
    Either<GeneralError, Boolean> consumePasswordResetToken(PasswordResetRequest passwordResetRequest);
}
