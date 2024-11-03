package org.library.thelibraryj.authentication.userAuth;

import io.vavr.control.Either;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserAuthResponse;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserAuthService extends UserDetailsService {
   Either<GeneralError, UserCreationResponse> createNewUser(UserCreationRequest userCreationRequest);
   boolean existsByEmail(String email);
   Either<GeneralError, Boolean> isEnabled(UUID userId);
   Either<GeneralError, Boolean> enableUser(UUID userId);
   Either<GeneralError, Boolean> disableUser(UUID userId);
   Either<GeneralError, Boolean> updatePassword(UUID userId, char[] newEncryptedPassword);
   Either<GeneralError, UserAuthResponse> getResponseByEmail(String email);
}
