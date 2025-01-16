package org.library.thelibraryj.authentication.userAuth.domain;

import org.library.thelibraryj.authentication.userAuth.dto.UserCreationData;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoWithImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
interface UserAuthMapper {
    UserAuth userAuthRequestToUserAuth(UserCreationRequest userCreationRequest);

    @Mapping(source = "userInfoResponse.username", target = "username")
    @Mapping(source = "userAuth.email", target = "email")
    @Mapping(source = "userAuth.id", target = "userAuthId")
    UserCreationData userAuthAndUserInfoResponseToUserCreationResponse(UserInfoWithImageResponse userInfoResponse, UserAuth userAuth);

    default Instant map(LocalDateTime value){
        return value.toInstant(ZoneOffset.ofHours(1));
    }
}
