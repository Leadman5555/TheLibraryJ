package org.library.thelibraryj.authentication.tokenServices.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.authentication.tokenServices.ActivationService;
import org.library.thelibraryj.authentication.tokenServices.dto.activation.ActivationTokenResponse;
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
class ActivationServiceImpl implements ActivationService {
    private final TokenRepository tokenRepository;
    private final UserAuthService userAuthService;

    @Value("${library.activation.expiration_time_seconds}")
    private long expirationTimeSeconds;

    public ActivationServiceImpl(TokenRepository tokenRepository, UserAuthService userAuthService) {
        this.tokenRepository = tokenRepository;
        this.userAuthService = userAuthService;
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
        Token newToken = Token.builder()
                .token(UUID.randomUUID())
                .expiresAt(Instant.now().plusSeconds(expirationTimeSeconds))
                .forUserId(idForToken)
                .isUsed(false)
                .build();
        tokenRepository.persist(newToken);
        return new ActivationTokenResponse(newToken.getToken().toString(), newToken.getExpiresAt());
    }

    @Transactional
    @Override
    public Either<GeneralError, Boolean> consumeActivationToken(UUID tokenId) {
        Either<GeneralError, Token> fetched = Try.of(() -> tokenRepository.findByToken(tokenId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new ActivationError.ActivationTokenNotFound(tokenId)));
        if(fetched.isLeft()) return Either.left(fetched.getLeft());
        Token token = fetched.get();
        if(token.hasExpired()) return Either.left(new ActivationError.ActivationTokenExpired(token.getToken()));
        if(token.isUsed()) return Either.left(new ActivationError.ActivationTokenAlreadyUsed(token.getToken()));
        token.setUsed(true);
        tokenRepository.update(token);
        return userAuthService.enableUser(token.getForUserId());
    }

    @Transactional
    @Override
    public void deleteAllUsedAndExpiredTokens() {
        tokenRepository.deleteAllUsedAndExpired();
    }
}
