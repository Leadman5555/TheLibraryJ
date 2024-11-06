package org.library.thelibraryj.authentication.activation.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.authentication.activation.ActivationService;
import org.library.thelibraryj.authentication.activation.dto.ActivationTokenResponse;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
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
class ActivationServiceImpl implements ActivationService {
    private final ActivationTokenRepository activationTokenRepository;
    private final UserAuthService userAuthService;

    @Value("${library.activation.expiration_time_seconds}")
    private long expirationTimeSeconds;

    public ActivationServiceImpl(ActivationTokenRepository activationTokenRepository, UserAuthService userAuthService) {
        this.activationTokenRepository = activationTokenRepository;
        this.userAuthService = userAuthService;
    }

    @Override
    public Either<GeneralError, ActivationTokenResponse> createActivationToken(UUID idForToken) {
        Either<GeneralError, Boolean> isEnabledQueryResult = userAuthService.isEnabled(idForToken);
        if(isEnabledQueryResult.isLeft()) return Either.left(isEnabledQueryResult.getLeft());
        if(isEnabledQueryResult.get()) return Either.left(new ActivationError.UserAlreadyEnabled(idForToken));
        return Either.right(createFirstActivationToken(idForToken));
    }

    @Transactional
    @Override
    public ActivationTokenResponse createFirstActivationToken(UUID idForToken) {
        ActivationToken newToken = ActivationToken.builder()
                .token(UUID.randomUUID())
                .expiresAt(Instant.now().plusSeconds(expirationTimeSeconds))
                .forUserId(idForToken)
                .isUsed(false)
                .build();
        activationTokenRepository.persist(newToken);
        return new ActivationTokenResponse(newToken.getToken().toString(), newToken.getExpiresAt());
    }

    @Transactional
    @Override
    public Either<GeneralError, Boolean> useActivationToken(UUID token) {
        Either<GeneralError, ActivationToken> fetched = Try.of(() -> activationTokenRepository.findByToken(token))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new ActivationError.ActivationTokenNotFound(token)));
        if(fetched.isLeft()) return Either.left(fetched.getLeft());
        ActivationToken activationToken = fetched.get();
        if(activationToken.hasExpired()) return Either.left(new ActivationError.ActivationTokenExpired(activationToken.getForUserId()));
        if(activationToken.isUsed()) return Either.left(new ActivationError.ActivationTokenAlreadyUsed(activationToken.getForUserId()));
        activationToken.setUsed(true);
        activationTokenRepository.update(activationToken);
        return userAuthService.enableUser(activationToken.getForUserId());
    }

    @Transactional
    @Override
    public void deleteAllUsedAndExpiredTokens() {
        activationTokenRepository.deleteAllUsedAndExpired();
    }
}
