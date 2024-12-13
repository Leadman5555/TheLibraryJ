package org.library.thelibraryj.authentication.userAuth;

import io.vavr.control.Either;
import org.library.thelibraryj.authentication.userAuth.domain.BasicUserAuthView;
import org.library.thelibraryj.authentication.userAuth.domain.LoginDataView;
import org.library.thelibraryj.authentication.userAuth.domain.PasswordResetView;
import org.library.thelibraryj.authentication.userAuth.dto.GoogleUserCreationRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationData;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationRequest;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserAuthService extends UserDetailsService {
   Either<GeneralError, UserCreationData> createNewUser(UserCreationRequest userCreationRequest);
   void createNewGoogleUser(GoogleUserCreationRequest userCreationRequest);
   boolean existsByEmail(String email);
   Either<GeneralError, BasicUserAuthView> getBasicUserAuthDataByEmail(String email);
   Either<GeneralError, Boolean> enableUser(UUID userId);
   Either<GeneralError, Boolean> disableUser(UUID userId);
   Either<GeneralError, Boolean> updatePassword(UUID userId, char[] newEncryptedPassword);
   Either<GeneralError, PasswordResetView> getPasswordResetDataByEmail(String email);
   Either<GeneralError, LoginDataView> getLoginDataByEmail(String email);
}
