package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.authentication.PasswordControl;
import org.library.thelibraryj.authentication.authTokenServices.PasswordResetTokenService;
import org.library.thelibraryj.authentication.authTokenServices.dto.password.PasswordResetRequest;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.domain.PasswordResetView;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.PasswordResetTemplate;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.PasswordResetError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserAuthError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
class PasswordResetTokenServiceImpl implements PasswordResetTokenService, PasswordControl {

    private final AuthTokenRepository authTokenRepository;
    private final EmailService emailService;
    private final UserAuthService userAuthService;
    private final PasswordResetProperties properties;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetTokenServiceImpl(AuthTokenRepository authTokenRepository, EmailService emailService, UserAuthService userAuthService, PasswordResetProperties passwordResetProperties, PasswordEncoder passwordEncoder) {
        this.authTokenRepository = authTokenRepository;
        this.emailService = emailService;
        this.userAuthService = userAuthService;
        this.properties = passwordResetProperties;
        this.passwordEncoder = passwordEncoder;
    }

    public Either<GeneralError, Boolean> startPasswordResetProcedure(String forEmail) {
        Either<GeneralError, PasswordResetView> fetchedData = userAuthService.getPasswordResetDataByEmail(forEmail);
        if(fetchedData.isLeft()) return Either.left(fetchedData.getLeft());
        if(fetchedData.get().getIsGoogleUser()) return Either.left(new UserAuthError.UserIsGoogleRegistered(forEmail));
        AuthToken newAuthToken = AuthToken.builder()
                .token(UUID.randomUUID())
                .expiresAt(Instant.now().plusSeconds(properties.getExpiration_time_seconds()))
                .forUserId(fetchedData.get().getUserAuthId())
                .isUsed(false)
                .build();
        authTokenRepository.persist(newAuthToken);
        emailService.sendEmail(new EmailRequest(
                forEmail,
                new PasswordResetTemplate(properties.getActivation_link() + newAuthToken.getToken(), newAuthToken.getExpiresAt())
        ));
        return Either.right(true);
    }

    public Either<GeneralError, Boolean> consumePasswordResetToken(PasswordResetRequest request) {
        Either<GeneralError, AuthToken> fetched = Try.of(() -> authTokenRepository.findByToken(request.tokenId()))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new PasswordResetError.PasswordResetTokenNotFound(request.tokenId())));
        if(fetched.isLeft()) return Either.left(fetched.getLeft());
        AuthToken authToken = fetched.get();
        if(authToken.hasExpired()) return Either.left(new PasswordResetError.PasswordResetTokenExpired(authToken.getId()));
        if(authToken.isUsed()) return Either.left(new PasswordResetError.PasswordResetTokenAlreadyUsed(authToken.getId()));
        authToken.setUsed(true);
        authTokenRepository.update(authToken);
        char[] encrypted = passwordEncoder.encode(new String(request.newPassword())).toCharArray();
        zeroPassword(request.newPassword());
        return userAuthService.updatePassword(authToken.getForUserId(), encrypted);
    }

    @Transactional
    @Override
    public void clearInvalidTokens() {
        this.authTokenRepository.deleteAllExpired();
    }
}
