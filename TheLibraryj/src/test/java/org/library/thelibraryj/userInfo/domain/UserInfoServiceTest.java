package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.userInfo.dto.request.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoUsernameUpdateRequest;
import org.library.thelibraryj.userInfo.dto.response.UserRankUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserUsernameUpdateResponse;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = UserInfoProperties.class)
public class UserInfoServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private BookService bookService;
    @Spy
    private UserInfoMapper userInfoMapper = new UserInfoMapperImpl();
    @Spy
    private UserInfoProperties userInfoProperties = new UserInfoProperties();
    private UserInfoServiceImpl userInfoService;

    private UUID userId;
    private String username;
    private UserInfo userInfo;
    private String userEmail;

    @BeforeEach
    public void setUp() {
//        when(userInfoProperties.getMinimal_age_hours()).thenReturn(24);
//        when(userInfoProperties.getUsername_change_cooldown_days()).thenReturn(90);
//        when(userInfoProperties.getRank_requirements()).thenReturn("3, 5, 10, 20, 40, 60, 100, 200, 500, 1000");
        userInfoProperties.setMinimal_age_hours(24);
        userInfoProperties.setUsername_change_cooldown_days(90);
        userInfoProperties.setRank_requirements("3, 5, 10, 20, 40, 60, 100, 200, 500, 1000");
        userId = UUID.randomUUID();
        username = "sample username";
        userEmail = "sample@example.com";
        Instant oldTime = (LocalDateTime.of(2000,10, 1,1,1).toInstant(ZoneOffset.UTC));
        userInfo = UserInfo.builder()
                .id(userId)
                .email(userEmail)
                .username(username)
                .rank(5)
                .createdAt(oldTime)
                .updatedAt(oldTime)
                .dataUpdatedAt(oldTime)
                .build();
        userInfoService = new UserInfoServiceImpl(userInfoRepository, userInfoMapper, userInfoProperties, null);
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
        Assertions.assertEquals(new UserInfoError.UsernameNotUnique(), response3.getLeft());

        verify(userInfoRepository, times(1)).update(userInfo);
    }
}
