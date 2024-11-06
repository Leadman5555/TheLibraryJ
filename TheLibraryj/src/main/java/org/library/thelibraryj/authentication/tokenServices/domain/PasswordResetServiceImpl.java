package org.library.thelibraryj.authentication.tokenServices.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.mail.MessagingException;
import org.library.thelibraryj.authentication.PasswordControl;
import org.library.thelibraryj.authentication.tokenServices.dto.password.PasswordResetRequest;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.PasswordResetTemplate;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.PasswordResetError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
class PasswordResetServiceImpl implements PasswordControl {

    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserAuthService userAuthService;
    private final PasswordResetProperties properties;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(TokenRepository tokenRepository, EmailService emailService, UserAuthService userAuthService, PasswordResetProperties passwordResetProperties, PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userAuthService = userAuthService;
        this.properties = passwordResetProperties;
        this.passwordEncoder = passwordEncoder;
    }

    public Either<GeneralError, Boolean> startPasswordResetProcedure(String forEmail) throws MessagingException {
        Either<GeneralError, UUID> fetchedAuthId = userAuthService.getAuthIdByEmail(forEmail);
        if(fetchedAuthId.isLeft()) return Either.left(fetchedAuthId.getLeft());
        Token newToken = Token.builder()
                .token(UUID.randomUUID())
                .expiresAt(Instant.now().plusSeconds(properties.getExpiration_time_seconds()))
                .forUserId(fetchedAuthId.get())
                .isUsed(false)
                .build();
        tokenRepository.persist(newToken);
        emailService.sendEmail(new EmailRequest(
                forEmail,
                new PasswordResetTemplate(properties.getActivation_link(), newToken.getExpiresAt())
        ));
        return Either.right(true);
    }

    public Either<GeneralError, Boolean> consumePasswordResetToken(PasswordResetRequest request) {
        Either<GeneralError, Token> fetched = Try.of(() -> tokenRepository.findByToken(request.tokenId()))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new PasswordResetError.PasswordResetTokenNotFound(request.tokenId())));
        if(fetched.isLeft()) return Either.left(fetched.getLeft());
        Token token = fetched.get();
        if(token.hasExpired()) return Either.left(new PasswordResetError.PasswordResetTokenExpired(token.getForUserId()));
        if(token.isUsed()) return Either.left(new PasswordResetError.PasswordResetTokenAlreadyUsed(token.getForUserId()));
        token.setUsed(true);
        tokenRepository.update(token);
        char[] encrypted = passwordEncoder.encode(new String(request.newPassword())).toCharArray();
        zeroPassword(request.newPassword());
        return userAuthService.updatePassword(token.getForUserId(), encrypted);
    }

}
