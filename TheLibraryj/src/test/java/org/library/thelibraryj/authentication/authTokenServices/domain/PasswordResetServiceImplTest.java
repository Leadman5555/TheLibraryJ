package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.authentication.authTokenServices.dto.password.PasswordResetRequest;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.PasswordResetError;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PasswordResetServiceImplTest {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private PasswordResetProperties properties;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    PasswordResetServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsumePasswordResetTokenSuccess() {
        UUID tokenId = UUID.randomUUID();
        PasswordResetRequest request = new PasswordResetRequest(tokenId, "newPassword123!".toCharArray());
        AuthToken authToken = AuthToken.builder()
                .id(UUID.randomUUID())
                .token(tokenId)
                .expiresAt(Instant.now().plusSeconds(3600))
                .forUserId(UUID.randomUUID())
                .isUsed(false)
                .build();

        when(authTokenRepository.findByToken(eq(tokenId))).thenReturn(Optional.of(authToken));
        when(passwordEncoder.encode(eq(new String(request.newPassword())))).thenReturn("encryptedPassword");
        when(userAuthService.updatePassword(eq(authToken.getForUserId()), eq("encryptedPassword".toCharArray())))
                .thenReturn(Either.right(true));

        Either<GeneralError, Boolean> result = passwordResetService.consumePasswordResetToken(request);

        assertTrue(result.isRight());
        assertEquals(true, result.get());
        verify(authTokenRepository, times(1)).update(eq(authToken));
        verify(userAuthService, times(1)).updatePassword(eq(authToken.getForUserId()), eq("encryptedPassword".toCharArray()));
    }

    @Test
    void testConsumePasswordResetTokenFailTokenNotFound() {
        UUID tokenId = UUID.randomUUID();
        PasswordResetRequest request = new PasswordResetRequest(tokenId, "newPassword123!".toCharArray());

        when(authTokenRepository.findByToken(eq(tokenId))).thenReturn(Optional.empty());

        Either<GeneralError, Boolean> result = passwordResetService.consumePasswordResetToken(request);

        assertTrue(result.isLeft());
        assertInstanceOf(PasswordResetError.PasswordResetTokenNotFound.class, result.getLeft());
        assertEquals(tokenId, ((PasswordResetError.PasswordResetTokenNotFound) result.getLeft()).tokenId());
        verifyNoInteractions(passwordEncoder, userAuthService);
    }

    @Test
    void testConsumePasswordResetTokenFailTokenExpired() {
        UUID tokenId = UUID.randomUUID();
        PasswordResetRequest request = new PasswordResetRequest(tokenId, "newPassword123!".toCharArray());
        AuthToken authToken = AuthToken.builder()
                .id(UUID.randomUUID())
                .token(tokenId)
                .expiresAt(Instant.now().minusSeconds(3600))
                .forUserId(UUID.randomUUID())
                .isUsed(false)
                .build();

        when(authTokenRepository.findByToken(eq(tokenId))).thenReturn(Optional.of(authToken));

        Either<GeneralError, Boolean> result = passwordResetService.consumePasswordResetToken(request);

        assertTrue(result.isLeft());
        assertInstanceOf(PasswordResetError.PasswordResetTokenExpired.class, result.getLeft());
        verifyNoInteractions(passwordEncoder, userAuthService);
    }

    @Test
    void testConsumePasswordResetTokenFailTokenAlreadyUsed() {
        UUID tokenId = UUID.randomUUID();
        PasswordResetRequest request = new PasswordResetRequest(tokenId, "newPassword123!".toCharArray());
        AuthToken authToken = AuthToken.builder()
                .id(UUID.randomUUID())
                .token(tokenId)
                .expiresAt(Instant.now().plusSeconds(3600))
                .forUserId(UUID.randomUUID())
                .isUsed(true)
                .build();

        when(authTokenRepository.findByToken(eq(tokenId))).thenReturn(Optional.of(authToken));

        Either<GeneralError, Boolean> result = passwordResetService.consumePasswordResetToken(request);

        assertTrue(result.isLeft());
        assertInstanceOf(PasswordResetError.PasswordResetTokenAlreadyUsed.class, result.getLeft());
        verifyNoInteractions(passwordEncoder, userAuthService);
    }
}