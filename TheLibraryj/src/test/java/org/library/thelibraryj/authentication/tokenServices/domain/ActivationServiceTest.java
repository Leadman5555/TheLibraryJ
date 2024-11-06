package org.library.thelibraryj.authentication.tokenServices.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.authentication.tokenServices.dto.activation.ActivationTokenResponse;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.infrastructure.error.errorTypes.ActivationError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActivationServiceTest {
    @Mock
    private UserAuthService userAuthService;
    @Mock
    private TokenRepository activationTokenRepository;
    private static final long expirationTimeSeconds = 800;
    @InjectMocks
    private ActivationServiceImpl activationService;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(activationService, "expirationTimeSeconds", expirationTimeSeconds);
    }

    @Test
    public void testCreateActivationToken() {
        when(userAuthService.isEnabled(userId)).thenReturn(Either.right(false));
        Instant expectedTime = Instant.now().plusSeconds(expirationTimeSeconds);
        Either<GeneralError, ActivationTokenResponse> response = activationService.createActivationToken(userId);
        verify(activationTokenRepository).persist(any(Token.class));
        Assertions.assertTrue(response.isRight());
        Assertions.assertTrue(response.get().expiresAt().minusSeconds(expectedTime.getEpochSecond()).getEpochSecond() < 5);
    }

    @Test
    public void testConsumeActivationToken() {
        UUID tokenId = UUID.randomUUID();
        Token activationToken = Token.builder()
                .token(UUID.randomUUID())
                .id(tokenId)
                .isUsed(false)
                .forUserId(userId)
                .expiresAt(Instant.now().plusSeconds(1000))
                .build();
        when(activationTokenRepository.findByToken(activationToken.getToken())).thenReturn(Optional.of(activationToken));
        when(userAuthService.enableUser(userId)).thenReturn(Either.right(true));
        Either<GeneralError, Boolean> result = activationService.consumeActivationToken(activationToken.getToken());
        verify(activationTokenRepository).update(any(Token.class));
        Assertions.assertTrue(result.isRight());
    }

    @Test
    public void testConsumeActivationTokenErrorUsed(){
        Token activationTokenUsed = Token.builder()
                .token(UUID.randomUUID())
                .id(UUID.randomUUID())
                .isUsed(true)
                .forUserId(userId)
                .expiresAt(Instant.now().plusSeconds(100000))
                .build();
        when(activationTokenRepository.findByToken(activationTokenUsed.getToken())).thenReturn(Optional.of(activationTokenUsed));
        Either<GeneralError, Boolean> result = activationService.consumeActivationToken(activationTokenUsed.getToken());
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals(new ActivationError.ActivationTokenAlreadyUsed(userId), result.getLeft());
    }

    @Test
    public void testConsumeActivationTokenErrorExpired(){
        Token activationTokenUsed = Token.builder()
                .token(UUID.randomUUID())
                .id(UUID.randomUUID())
                .isUsed(false)
                .forUserId(userId)
                .expiresAt(Instant.now().minusSeconds(100000))
                .build();
        when(activationTokenRepository.findByToken(activationTokenUsed.getToken())).thenReturn(Optional.of(activationTokenUsed));
        Either<GeneralError, Boolean> result = activationService.consumeActivationToken(activationTokenUsed.getToken());
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals(new ActivationError.ActivationTokenExpired(userId), result.getLeft());
    }

    @Test
    public void testConsumeActivationTokenErrorNotFound(){
        UUID tokenId = UUID.randomUUID();
        when(activationTokenRepository.findByToken(tokenId)).thenReturn(Optional.empty());
        Either<GeneralError, Boolean> result = activationService.consumeActivationToken(tokenId);
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals(new ActivationError.ActivationTokenNotFound(tokenId), result.getLeft());

    }

}
