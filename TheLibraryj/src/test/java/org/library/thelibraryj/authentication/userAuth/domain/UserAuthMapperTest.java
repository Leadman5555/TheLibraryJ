package org.library.thelibraryj.authentication.userAuth.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationData;
import org.library.thelibraryj.userInfo.dto.UserInfoWithImageResponse;

import java.time.Instant;
import java.util.UUID;

public class UserAuthMapperTest {

    @Test
    public void testUserCreationResponseMapping() {
        UserAuthMapper userAuthMapper = new UserAuthMapperImpl();
        final UUID userAuthId = UUID.randomUUID();
        final String userEmail = "sample@email.com";
        UserAuth userAuth = UserAuth.builder()
                .id(userAuthId)
                .email(userEmail)
                .role(UserRole.USER)
                .password(("password").toCharArray())
                .isEnabled(true)
                .build();
        UserInfoWithImageResponse userInfo = new UserInfoWithImageResponse(
                "username",
                userEmail,
                4,
                20,
                Instant.now(),
                null,
                (short) 0,
                null
        );
        UserCreationData mapped = userAuthMapper.userAuthAndUserInfoResponseToUserCreationResponse(userInfo, userAuth);
        Assertions.assertAll(
                () -> Assertions.assertEquals(userAuth.getId(), mapped.userAuthId()),
                () -> Assertions.assertEquals(userAuth.getEmail(), mapped.email()),
                () -> Assertions.assertEquals(userInfo.username(), mapped.username()),
                () -> Assertions.assertEquals(userInfo.dataUpdatedAt(), mapped.dataUpdatedAt()),
                () -> Assertions.assertEquals(userAuth.isEnabled(), mapped.isEnabled()),
                () -> Assertions.assertEquals(userInfo.rank(), mapped.rank()),
                () -> Assertions.assertEquals(userInfo.currentScore(), mapped.currentScore())
                );
    }
}
