package org.library.thelibraryj.authentication.activation.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.authentication.activation.dto.ActivationTokenResponse;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.infrastructure.error.errorTypes.ActivationError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Objects;
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
    private ActivationTokenRepository activationTokenRepository;
    private static final long expirationTimeSeconds = 80000;
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
        verify(activationTokenRepository).persist(any(ActivationToken.class));
        Assertions.assertTrue(response.isRight());
        Assertions.assertTrue(response.get().expiresAt().minusSeconds(expectedTime.getEpochSecond()).getEpochSecond() < 5);
    }

    @Test
    public void testUseActivationToken() {
        UUID tokenId = UUID.randomUUID();
        ActivationToken activationToken = ActivationToken.builder()
                .token(UUID.randomUUID())
                .id(tokenId)
                .isUsed(false)
                .forUserId(userId)
                .expiresAt(Instant.now().plusSeconds(1000))
                .build();
        when(activationTokenRepository.findById(tokenId)).thenReturn(Optional.ofNullable(activationToken));
        Either<GeneralError, Boolean> result = activationService.useActivationToken(tokenId);
        verify(activationTokenRepository).update(activationToken);
        Assertions.assertTrue(result.isRight());
        Objects.requireNonNull(activationToken).setUsed(true);
        result = activationService.useActivationToken(tokenId);
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals(new ActivationError.ActivationTokenAlreadyUsed(userId), result.getLeft());
        ActivationToken activationTokenExpired = ActivationToken.builder()
                .token(UUID.randomUUID())
                .id(tokenId)
                .isUsed(false)
                .forUserId(userId)
                .expiresAt(Instant.now().minusSeconds(10000))
                .build();
        when(activationTokenRepository.findById(tokenId)).thenReturn(Optional.ofNullable(activationTokenExpired));
        result = activationService.useActivationToken(tokenId);
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals(new ActivationError.ActivationTokenExpired(userId), result.getLeft());
        when(activationTokenRepository.findById(tokenId)).thenReturn(Optional.empty());
        result = activationService.useActivationToken(tokenId);
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals(new ActivationError.ActivationTokenNotFound(tokenId), result.getLeft());
    }

}
