package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.authentication.authTokenServices.dto.activation.ActivationTokenResponse;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.domain.BasicUserAuthView;
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
    private AuthTokenRepository activationAuthTokenRepository;
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
        final String userEmail = "sample@email.com";
        when(userAuthService.getBasicUserAuthDataByEmail(userEmail)).thenReturn(Either.right(new BasicUserAuthView() {
            @Override
            public UUID getUserAuthId() {
                return userId;
            }

            @Override
            public boolean getIsEnabled() {
                return false;
            }
        }));
        Instant expectedTime = Instant.now().plusSeconds(expirationTimeSeconds);
        Either<GeneralError, ActivationTokenResponse> response = activationService.createActivationToken(userEmail);
        verify(activationAuthTokenRepository).persist(any(AuthToken.class));
        Assertions.assertTrue(response.isRight());
        Assertions.assertTrue(response.get().expiresAt().minusSeconds(expectedTime.getEpochSecond()).getEpochSecond() < 5);
    }

    @Test
    public void testConsumeActivationToken() {
        UUID tokenId = UUID.randomUUID();
        AuthToken activationAuthToken = AuthToken.builder()
                .token(UUID.randomUUID())
                .id(tokenId)
                .isUsed(false)
                .forUserId(userId)
                .expiresAt(Instant.now().plusSeconds(1000))
                .build();
        when(activationAuthTokenRepository.findByToken(activationAuthToken.getToken())).thenReturn(Optional.of(activationAuthToken));
        when(userAuthService.enableUser(userId)).thenReturn(Either.right(true));
        Either<GeneralError, Boolean> result = activationService.consumeActivationToken(activationAuthToken.getToken());
        verify(activationAuthTokenRepository).update(any(AuthToken.class));
        Assertions.assertTrue(result.isRight());
    }

    @Test
    public void testConsumeActivationTokenErrorUsed(){
        AuthToken activationAuthTokenUsed = AuthToken.builder()
                .token(UUID.randomUUID())
                .id(UUID.randomUUID())
                .isUsed(true)
                .forUserId(userId)
                .expiresAt(Instant.now().plusSeconds(100000))
                .build();
        when(activationAuthTokenRepository.findByToken(activationAuthTokenUsed.getToken())).thenReturn(Optional.of(activationAuthTokenUsed));
        Either<GeneralError, Boolean> result = activationService.consumeActivationToken(activationAuthTokenUsed.getToken());
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals(new ActivationError.ActivationTokenAlreadyUsed(activationAuthTokenUsed.getToken()), result.getLeft());
    }

    @Test
    public void testConsumeActivationTokenErrorExpired(){
        AuthToken activationAuthTokenExpired = AuthToken.builder()
                .token(UUID.randomUUID())
                .id(UUID.randomUUID())
                .isUsed(false)
                .forUserId(userId)
                .expiresAt(Instant.now().minusSeconds(100000))
                .build();
        when(activationAuthTokenRepository.findByToken(activationAuthTokenExpired.getToken())).thenReturn(Optional.of(activationAuthTokenExpired));
        Either<GeneralError, Boolean> result = activationService.consumeActivationToken(activationAuthTokenExpired.getToken());
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals(new ActivationError.ActivationTokenExpired(activationAuthTokenExpired.getToken()), result.getLeft());
    }

    @Test
    public void testConsumeActivationTokenErrorNotFound(){
        UUID tokenId = UUID.randomUUID();
        when(activationAuthTokenRepository.findByToken(tokenId)).thenReturn(Optional.empty());
        Either<GeneralError, Boolean> result = activationService.consumeActivationToken(tokenId);
        Assertions.assertTrue(result.isLeft());
        Assertions.assertEquals(new ActivationError.ActivationTokenNotFound(tokenId), result.getLeft());

    }

}
