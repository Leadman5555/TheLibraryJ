package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.userInfo.dto.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoUsernameUpdateRequest;
import org.mockito.InjectMocks;
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
@ContextConfiguration(classes = UserInfoConfig.class)
public class UserInfoServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private BookService bookService;
    @Spy
    private UserInfoConfig userInfoConfig = new UserInfoConfig();
    @Spy
    private UserInfoMapper userInfoMapper = new UserInfoMapperImpl();
    @InjectMocks
    private UserInfoService userInfoService;

    private UUID userId;
    private String username;
    private UserInfo userInfo;

    @BeforeEach
    public void setUp() {
        userInfoService.setBookService(bookService);
        userInfoConfig.setMinimal_age_hours(24);
        userInfoConfig.setUsername_change_cooldown_days(90);
        userId = UUID.randomUUID();
        username = "sample username";
        Instant oldTime = (LocalDateTime.of(2000,10, 1,1,1).toInstant(ZoneOffset.UTC));
        userInfo = UserInfo.builder()
                .id(userId)
                .username(username)
                .rank(5)
                .createdAt(oldTime)
                .updatedAt(oldTime)
                .dataUpdatedAt(oldTime)
                .build();
    }


    @Test
    public void testGetAuthorUsernameAndCheckAccountAge(){
        when(userInfoRepository.findById(userId)).thenReturn(Optional.ofNullable(userInfo));
        Either<GeneralError, String> response = userInfoService.getAuthorUsernameAndCheckAccountAge(userId);
        Assertions.assertTrue(response.isRight());
        Assertions.assertEquals(username, response.get());

        userInfo.setCreatedAt(Instant.now());
        Either<GeneralError, String> response2 = userInfoService.getAuthorUsernameAndCheckAccountAge(userId);
        Assertions.assertFalse(response2.isRight());
    }

    @Test
    public void testUpdateRank(){
        when(userInfoRepository.findById(userId)).thenReturn(Optional.ofNullable(userInfo));
        UserInfoRankUpdateRequest request = new UserInfoRankUpdateRequest(userInfo.getId(), 10);
        Either<GeneralError, UserInfoResponse> response = userInfoService.updateRank(request);
        Assertions.assertTrue(response.isRight());
        Assertions.assertEquals(10, response.get().rank());
        verify(userInfoRepository).update(userInfo);
    }

    @Test
    public void testUpdateUserInfoUsername(){
        final String newUsername = "new username";
        when(userInfoRepository.existsByUsername(newUsername)).thenReturn(false);
        when(userInfoRepository.findById(userId)).thenReturn(Optional.ofNullable(userInfo));
        UserInfoUsernameUpdateRequest request = new UserInfoUsernameUpdateRequest(userId, newUsername);
        Either<GeneralError, UserInfoResponse> response = userInfoService.updateUserInfoUsername(request);
        Assertions.assertTrue(response.isRight());
        Assertions.assertEquals(newUsername, response.get().username());

        userInfo.setDataUpdatedAt(Instant.now());
        Either<GeneralError, UserInfoResponse> response2 = userInfoService.updateUserInfoUsername(request);
        Assertions.assertFalse(response2.isRight());
        Assertions.assertEquals(new UserInfoError.UsernameUpdateCooldown(userInfoConfig.getUsername_change_cooldown_days()), response2.getLeft());

        when(userInfoRepository.existsByUsername(newUsername)).thenReturn(true);
        Either<GeneralError, UserInfoResponse> response3 = userInfoService.updateUserInfoUsername(request);
        Assertions.assertFalse(response3.isRight());
        Assertions.assertEquals(new UserInfoError.UsernameNotUnique(), response3.getLeft());

        verify(userInfoRepository, times(1)).update(userInfo);
    }
}
