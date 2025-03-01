package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.infrastructure.textParsers.inputParsers.HtmlEscaper;
import org.library.thelibraryj.userInfo.dto.request.FavouriteBookMergerRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoUsernameUpdateRequest;
import org.library.thelibraryj.userInfo.dto.response.FavouriteBookMergerResponse;
import org.library.thelibraryj.userInfo.dto.response.UserRankUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserUsernameUpdateResponse;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@SuppressWarnings("FieldCanBeLocal")
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = UserInfoProperties.class)
public class UserInfoServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private BookService bookService;
    @Spy
    private final UserInfoMapper userInfoMapper = new UserInfoMapperImpl();
    @Spy
    private final UserInfoProperties userInfoProperties = new UserInfoProperties();
    private UserInfoServiceImpl userInfoService;

    private UUID userId;
    private String username;
    private UserInfo userInfo;
    private String userEmail;
    private UUID userId2;
    private String username2;
    private UserInfo userInfo2;
    private String userEmail2;

    @BeforeEach
    public void setUp() {
        userInfoProperties.setMinimal_age_hours(24);
        userInfoProperties.setUsername_change_cooldown_days(90);
        userInfoProperties.setRank_requirements("3, 5, 10, 20, 40, 60, 100, 200, 500, 1000");
        userId = UUID.randomUUID();
        username = "sample username";
        userEmail = "sample@example.com";
        Instant oldTime = (LocalDateTime.of(2000,10, 1,1,1).toInstant(ZoneOffset.UTC));
        UUID bookId = UUID.randomUUID();
        userInfo = UserInfo.builder()
                .id(userId)
                .email(userEmail)
                .username(username)
                .rank((short) 5)
                .createdAt(oldTime)
                .updatedAt(oldTime)
                .dataUpdatedAt(oldTime)
                .build();
        userInfo.addBookIdToFavourites(bookId);
        userInfo.addBookIdToFavourites(UUID.randomUUID());
        userInfo.addBookIdToFavourites(UUID.randomUUID());
        userEmail2 = "sample@example.com2";
        userId2 = UUID.randomUUID();
        username2 = "sample username2";
        userInfo2 = UserInfo.builder()
                .id(userId2)
                .email(userEmail2)
                .username(username2)
                .rank((short) 3)
                .createdAt(oldTime)
                .updatedAt(oldTime)
                .dataUpdatedAt(oldTime)
                .build();
        userInfo2.addBookIdToFavourites(bookId);
        userInfoService = new UserInfoServiceImpl(userInfoRepository, userInfoMapper, userInfoProperties, null, new HtmlEscaper(false));
        userInfoService.setBookService(bookService);
    }


    @Test
    public void testGetAndValidateAuthorData(){
        when(userInfoRepository.getBookCreationUserView(userEmail)).thenReturn(Optional.of(new BookCreationUserView() {
            @Override
            public UUID getAuthorId() {
                return userId;
            }

            @Override
            public String getAuthorUsername() {
                return username;
            }

            @Override
            public Instant getCreatedAt() {
                return Instant.now().minusSeconds(10000000);
            }
        }));
        Either<GeneralError, BookCreationUserView> response = userInfoService.getAndValidateAuthorData(userEmail);
        Assertions.assertTrue(response.isRight());
        Assertions.assertEquals(username, response.get().getAuthorUsername());

        when(userInfoRepository.getBookCreationUserView(userEmail)).thenReturn(Optional.of(new BookCreationUserView() {
            @Override
            public UUID getAuthorId() {
                return userId;
            }

            @Override
            public String getAuthorUsername() {
                return username;
            }

            @Override
            public Instant getCreatedAt() {
                return Instant.now();
            }
        }));
        Either<GeneralError, BookCreationUserView> response2 = userInfoService.getAndValidateAuthorData(userEmail);
        Assertions.assertTrue(response2.isLeft());
    }

    @Test
    public void testUpdateRank(){
        userInfo.setCurrentScore(600);
        when(userInfoRepository.getByEmail(userEmail)).thenReturn(Optional.ofNullable(userInfo));
        Either<GeneralError, UserRankUpdateResponse> response = userInfoService.updateRank(userEmail);
        Assertions.assertTrue(response.isRight());
        Assertions.assertEquals(8 , response.get().newRank());
        Assertions.assertEquals(240, response.get().newScore());
        verify(userInfoRepository).update(userInfo);
    }

    @Test
    public void testForceUpdateRank(){
        when(userInfoRepository.getByEmail(userEmail)).thenReturn(Optional.ofNullable(userInfo));
        UserInfoRankUpdateRequest request = new UserInfoRankUpdateRequest(userEmail, 10);
        Either<GeneralError, UserRankUpdateResponse> response = userInfoService.forceUpdateRank(request);
        Assertions.assertTrue(response.isRight());
        Assertions.assertEquals(10, response.get().newRank());
        verify(userInfoRepository).update(userInfo);
    }

    @Test
    public void testUpdateUserInfoUsername(){
        final String newUsername = "new username";
        when(userInfoRepository.existsByUsername(newUsername)).thenReturn(false);
        when(userInfoRepository.getByEmail(userEmail)).thenReturn(Optional.ofNullable(userInfo));
        UserInfoUsernameUpdateRequest request = new UserInfoUsernameUpdateRequest(userEmail, newUsername);
        Either<GeneralError, UserUsernameUpdateResponse> response = userInfoService.updateUserInfoUsername(request);
        Assertions.assertTrue(response.isRight());
        Assertions.assertEquals(newUsername, response.get().newUsername());

        userInfo.setDataUpdatedAt(Instant.now());
        Either<GeneralError, UserUsernameUpdateResponse> response2 = userInfoService.updateUserInfoUsername(request);
        Assertions.assertFalse(response2.isRight());
        Assertions.assertEquals(new UserInfoError.UsernameUpdateCooldown(userInfoProperties.getUsername_change_cooldown_days(), userEmail), response2.getLeft());

        when(userInfoRepository.existsByUsername(newUsername)).thenReturn(true);
        Either<GeneralError, UserUsernameUpdateResponse> response3 = userInfoService.updateUserInfoUsername(request);
        Assertions.assertFalse(response3.isRight());
        Assertions.assertEquals(new UserInfoError.UsernameNotUnique(userEmail), response3.getLeft());

        verify(userInfoRepository, times(1)).update(userInfo);
    }

    @Test
    public void shouldMergeFavouriteBooks(){
        when(userInfoRepository.fetchUserInfoEagerById(userId)).thenReturn(Optional.of(userInfo));
        when(userInfoRepository.fetchUserInfoEagerByEmail(userEmail2)).thenReturn(Optional.of(userInfo2));
        int sizeBeforeMerge = userInfo2.getFavouriteBookIds().size();
        int attemptedToMergeCount = userInfo.getFavouriteBookIds().size();
        HashSet<UUID> copySet = new HashSet<>(userInfo2.getFavouriteBookIds());
        copySet.addAll(userInfo.getFavouriteBookIds());
        int expectedSizeAfterMerge = copySet.size();

        FavouriteBookMergerRequest request = new FavouriteBookMergerRequest(userId, userEmail2);
        Either<GeneralError, FavouriteBookMergerResponse> response = userInfoService.mergeFavouriteBooks(request);
        Assertions.assertTrue(response.isRight());
        FavouriteBookMergerResponse body = response.get();
        Assertions.assertEquals(attemptedToMergeCount, body.attemptedToMergeCount());
        Assertions.assertEquals(sizeBeforeMerge, body.sizeBeforeMerge());
        Assertions.assertEquals(expectedSizeAfterMerge, body.attemptedToMergeCount());
        Assertions.assertEquals(username, body.fromUsername());
        Assertions.assertEquals(username2, body.toUsername());

        verify(userInfoRepository, never()).update(userInfo);
        verify(userInfoRepository).update(userInfo2);
    }

    @Test
    public void shouldFailToMergeFavouriteBooks(){
        when(userInfoRepository.fetchUserInfoEagerById(userId)).thenReturn(Optional.of(userInfo));
        when(userInfoRepository.fetchUserInfoEagerByEmail(userEmail)).thenReturn(Optional.of(userInfo));

        FavouriteBookMergerRequest request = new FavouriteBookMergerRequest(userId, userEmail);
        Either<GeneralError, FavouriteBookMergerResponse> response = userInfoService.mergeFavouriteBooks(request);
        Assertions.assertTrue(response.isLeft());

        verify(userInfoRepository, never()).update(userInfo);
    }
}
