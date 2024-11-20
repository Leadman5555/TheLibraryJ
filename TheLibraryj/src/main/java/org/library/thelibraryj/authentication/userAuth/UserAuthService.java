package org.library.thelibraryj.authentication.userAuth;

import io.vavr.control.Either;
import org.library.thelibraryj.authentication.userAuth.dto.*;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationData;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserAuthService extends UserDetailsService {
   Either<GeneralError, UserCreationData> createNewUser(UserCreationRequest userCreationRequest);
   void createNewGoogleUser(GoogleUserCreationRequest userCreationRequest);
   boolean existsByEmail(String email);
   Either<GeneralError, BasicUserAuthData> getBasicUserAuthDataByEmail(String email);
   Either<GeneralError, Boolean> enableUser(UUID userId);
   Either<GeneralError, Boolean> disableUser(UUID userId);
   Either<GeneralError, Boolean> updatePassword(UUID userId, char[] newEncryptedPassword);
   Either<GeneralError, PasswordResetDataResponse> getPasswordResetDataByEmail(String email);
   Either<GeneralError, LoginDataResponse> getLoginDataByEmail(String email);
}
