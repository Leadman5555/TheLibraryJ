package org.library.thelibraryj.authentication.userAuth.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;

import java.time.Instant;
import java.util.UUID;

public class UserAuthMapperTest {

    @Test
    public void testUserCreationResponseMapping() {
        UserAuthMapper userAuthMapper = new UserAuthMapperImpl();
        UserAuth userAuth = UserAuth.builder()
                .id(UUID.randomUUID())
                .email("sample@email.com")
                .role(UserRole.USER)
                .password(("password").toCharArray())
                .isEnabled(true)
                .build();
        UserInfoResponse userInfo = new UserInfoResponse(
                UUID.randomUUID(),
                "username",
                userAuth.getEmail(),
                4,
                Instant.now()
        );
        UserCreationResponse mapped = userAuthMapper.userAuthAndUserInfoResponseToUserCreationResponse(userInfo, userAuth);
        Assertions.assertAll(
                ()-> Assertions.assertEquals(userInfo.userId(), mapped.userId()),
                () ->Assertions.assertEquals(userAuth.getEmail(), mapped.email()),
                () -> Assertions.assertEquals(userInfo.username(), mapped.username()),
                () -> Assertions.assertEquals(userInfo.dataUpdatedAt(), mapped.dataUpdatedAt()),
                () -> Assertions.assertEquals(userAuth.isEnabled(), mapped.isEnabled()),
                () -> Assertions.assertEquals(userInfo.rank(), mapped.rank())
        );
    }
}
