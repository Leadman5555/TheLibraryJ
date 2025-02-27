package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.authentication.authTokenServices.ActivationTokenService;
import org.library.thelibraryj.authentication.authTokenServices.dto.activation.ActivationTokenResponse;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.domain.BasicUserAuthView;
import org.library.thelibraryj.infrastructure.error.errorTypes.ActivationError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
class ActivationTokenServiceImpl implements ActivationTokenService {
    private final AuthTokenRepository authTokenRepository;
    private final UserAuthService userAuthService;
    private final long expiration_time_seconds;

    public ActivationTokenServiceImpl(@Value("${library.activation.expiration_time_seconds}") long expiration_time_seconds, AuthTokenRepository authTokenRepository, UserAuthService userAuthService) {
        this.authTokenRepository = authTokenRepository;
        this.userAuthService = userAuthService;
        this.expiration_time_seconds = expiration_time_seconds;
    }

    @Transactional
    @Override
    public Either<GeneralError, ActivationTokenResponse> createActivationToken(String forEmail) {
        Either<GeneralError, BasicUserAuthView> basicAuthDataQueryResult = userAuthService.getBasicUserAuthDataByEmail(forEmail);
        if(basicAuthDataQueryResult.isLeft()) return Either.left(basicAuthDataQueryResult.getLeft());
        BasicUserAuthView basicUserAuthView = basicAuthDataQueryResult.get();
        if(basicUserAuthView.getIsEnabled()) return Either.left(new ActivationError.UserAlreadyEnabled(forEmail));
        return Either.right(createFirstActivationToken(basicUserAuthView.getUserAuthId()));
    }

    @Transactional
    @Override
    public ActivationTokenResponse createFirstActivationToken(UUID idForToken) {
        AuthToken newAuthToken = AuthToken.builder()
                .token(UUID.randomUUID())
                .expiresAt(Instant.now().plusSeconds(expiration_time_seconds))
                .forUserId(idForToken)
                .isUsed(false)
                .build();
        authTokenRepository.persist(newAuthToken);
        return new ActivationTokenResponse(newAuthToken.getToken().toString(), newAuthToken.getExpiresAt());
    }

    @Transactional
    @Override
    public Either<GeneralError, Boolean> consumeActivationToken(UUID tokenId) {
        Either<GeneralError, AuthToken> fetched = Try.of(() -> authTokenRepository.findByToken(tokenId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new ActivationError.ActivationTokenNotFound(tokenId)));
        if(fetched.isLeft()) return Either.left(fetched.getLeft());
        AuthToken authToken = fetched.get();
        if(authToken.hasExpired()) return Either.left(new ActivationError.ActivationTokenExpired(authToken.getToken()));
        if(authToken.isUsed()) return Either.left(new ActivationError.ActivationTokenAlreadyUsed(authToken.getToken()));
        authToken.setUsed(true);
        authTokenRepository.update(authToken);
        return userAuthService.enableUser(authToken.getForUserId());
    }

    @Transactional
    @Override
    public void clearInvalidTokens() {
        authTokenRepository.deleteAllExpired();
    }
}
