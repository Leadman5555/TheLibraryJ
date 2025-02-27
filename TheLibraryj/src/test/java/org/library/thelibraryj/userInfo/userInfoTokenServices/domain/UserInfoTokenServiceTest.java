package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.request.FavouriteBookMergerRequest;
import org.library.thelibraryj.userInfo.dto.response.FavouriteBookMergerResponse;
import org.library.thelibraryj.userInfo.userInfoTokenServices.UserInfoTokenService;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenConsummationRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.response.BookTokenResponse;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserInfoTokenServiceTest {

    @Mock
    private EmailService emailService;
    @Mock
    private UserInfoTokenServicesViewRepository userInfoTokenServicesViewRepository;
    @Mock
    private FavouriteBookTokenRepository favouriteBookTokenRepository;
    @Mock
    private UserInfoService userInfoService;
    private static final Long expirationTimeSeconds = 800L;

    private UserInfoTokenService userInfoTokenService;

    @BeforeEach
    public void setUp() {
        userInfoTokenService = new UserInfoTokenServiceImpl(expirationTimeSeconds, userInfoService, favouriteBookTokenRepository, userInfoTokenServicesViewRepository, emailService);
    }


    @Test
    void shouldCreateNewTokenWhenNoExistingTokenIsFound() {
        String email = "test@example.com";
        UUID userId = UUID.randomUUID();
        BookTokenRequest request = new BookTokenRequest(email);

        when(userInfoService.getUserInfoIdByEmail(email)).thenReturn(Either.right(userId));
        when(userInfoTokenServicesViewRepository.fetchByUserId(userId)).thenReturn(Optional.empty());

        FavouriteBookToken newToken = FavouriteBookToken.builder()
                .token(UUID.randomUUID())
                .expiresAt(Instant.now().plusSeconds(expirationTimeSeconds))
                .forUserId(userId)
                .useCount(0)
                .build();

        when(favouriteBookTokenRepository.persist(any())).thenReturn(newToken);

        Either<GeneralError, BookTokenResponse> result = userInfoTokenService.upsertFavouriteBookToken(request);

        assertTrue(result.isRight());
        BookTokenResponse response = result.get();
        assertEquals(newToken.getToken().toString(), response.token());
        assertEquals(newToken.getExpiresAt(), response.expiresAt());
        assertEquals(newToken.getUseCount(), response.useCount());
        assertTrue(response.justCreated());

        verify(favouriteBookTokenRepository).persist(any());
    }

    @Test
    void shouldReturnExistingTokenIfNotExpired() {
        String email = "test@example.com";
        UUID userId = UUID.randomUUID();
        UUID token = UUID.randomUUID();
        BookTokenRequest request = new BookTokenRequest(email);
        Instant expectedExpiresAt = Instant.now().plusSeconds(expirationTimeSeconds);

        MiniFavouriteBookTokenView existingTokenView = new MiniFavouriteBookTokenView() {
            @Override
            public Instant getExpiresAt() {
                return expectedExpiresAt;
            }

            @Override
            public int getUseCount() {
                return 0;
            }

            @Override
            public UUID getToken() {
                return token;
            }
        };

        when(userInfoService.getUserInfoIdByEmail(email)).thenReturn(Either.right(userId));
        when(userInfoTokenServicesViewRepository.fetchByUserId(userId)).thenReturn(Optional.of(existingTokenView));

        Either<GeneralError, BookTokenResponse> result = userInfoTokenService.upsertFavouriteBookToken(request);

        assertTrue(result.isRight());
        BookTokenResponse response = result.get();
        assertEquals(token.toString(), response.token());
        assertEquals(existingTokenView.getExpiresAt(), response.expiresAt());
        assertEquals(existingTokenView.getUseCount(), response.useCount());
        assertFalse(response.justCreated());

        verify(favouriteBookTokenRepository, never()).persist(any());
    }

    @Test
    void shouldCreateNewTokenWhenExistingTokenIsExpired() {
        String email = "test@example.com";
        UUID userId = UUID.randomUUID();
        UUID expiredToken = UUID.randomUUID();
        BookTokenRequest request = new BookTokenRequest(email);

        MiniFavouriteBookTokenView expiredTokenView = new MiniFavouriteBookTokenView() {
            @Override
            public Instant getExpiresAt() {
                return Instant.now().minusSeconds(expirationTimeSeconds);
            }

            @Override
            public int getUseCount() {
                return 0;
            }

            @Override
            public UUID getToken() {
                return expiredToken;
            }
        };

        when(userInfoService.getUserInfoIdByEmail(email)).thenReturn(Either.right(userId));
        when(userInfoTokenServicesViewRepository.fetchByUserId(userId)).thenReturn(Optional.of(expiredTokenView));

        FavouriteBookToken newToken = FavouriteBookToken.builder()
                .token(UUID.randomUUID())
                .expiresAt(Instant.now().plusSeconds(expirationTimeSeconds))
                .forUserId(userId)
                .useCount(0)
                .build();

        when(favouriteBookTokenRepository.persist(any())).thenReturn(newToken);

        Either<GeneralError, BookTokenResponse> result = userInfoTokenService.upsertFavouriteBookToken(request);

        assertTrue(result.isRight());
        BookTokenResponse response = result.get();
        assertEquals(newToken.getToken().toString(), response.token());
        assertEquals(newToken.getExpiresAt(), response.expiresAt());
        assertEquals(newToken.getUseCount(), response.useCount());
        assertTrue(response.justCreated());

        verify(favouriteBookTokenRepository).persist(any());
    }

    @Test
    void shouldReturnErrorWhenUserIdCannotBeFetched() {
        String email = "invalid@example.com";
        BookTokenRequest request = new BookTokenRequest(email);
        UUID userId = UUID.randomUUID();

        GeneralError error = new UserInfoError.UserInfoEntityNotFoundById(userId);
        when(userInfoService.getUserInfoIdByEmail(email)).thenReturn(Either.left(error));

        Either<GeneralError, BookTokenResponse> result = userInfoTokenService.upsertFavouriteBookToken(request);

        assertTrue(result.isLeft());
        assertEquals(error, result.getLeft());

        verifyNoInteractions(userInfoTokenServicesViewRepository);
        verifyNoInteractions(favouriteBookTokenRepository);
    }

    @Test
    void shouldConsumeAndUpdateBookToken(){
        String email = "test@example.com";
        UUID token = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookTokenConsummationRequest request = new BookTokenConsummationRequest(token, email);
        when(userInfoTokenServicesViewRepository.fetchByToken(token)).thenReturn(Optional.of(new EssentialFavouriteBookTokenView() {
            @Override
            public Instant getExpiresAt() {
                return Instant.now().plusSeconds(expirationTimeSeconds);
            }

            @Override
            public UUID getUserId() {
                return userId;
            }
        }));
        FavouriteBookMergerRequest mergerRequest = new FavouriteBookMergerRequest(userId, email);
        FavouriteBookMergerResponse response = new FavouriteBookMergerResponse(0, 2, 2, "any", "any");
        when(userInfoService.mergeFavouriteBooks(mergerRequest)).thenReturn(Either.right(response));

        Either<GeneralError, FavouriteBookMergerResponse> result = userInfoTokenService.consumeFavouriteBookToken(request);
        assertTrue(result.isRight());
        assertEquals(response, result.get());

        verify(favouriteBookTokenRepository).incrementTokenUsage(token, 1);
        verify(userInfoService).mergeFavouriteBooks(mergerRequest);
    }

    @Test
    void shouldSendEmailWithValidToken(){
        String email = "test@example.com";
        UUID token = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        int useCount = 5;
        when(userInfoService.getUserInfoIdByEmail(email)).thenReturn(Either.right(userId));
        BookTokenConsummationRequest request = new BookTokenConsummationRequest(token, email);
        MiniFavouriteBookTokenView existingTokenView = new MiniFavouriteBookTokenView() {
            @Override
            public Instant getExpiresAt() {
                return Instant.now().plusSeconds(expirationTimeSeconds);
            }

            @Override
            public int getUseCount() {
                return useCount;
            }

            @Override
            public UUID getToken() {
                return token;
            }
        };
        when(userInfoTokenServicesViewRepository.fetchByUserIdAndToken(userId, existingTokenView.getToken())).thenReturn(Optional.of(existingTokenView));
        Either<GeneralError, Boolean> result = userInfoTokenService.sendTokenToEmail(request);
        Assertions.assertTrue(result.isRight());


        verify(emailService).sendEmail(new EmailRequest(email, any()));
    }
}
