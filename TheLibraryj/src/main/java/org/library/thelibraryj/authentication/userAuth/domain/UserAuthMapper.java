package org.library.thelibraryj.authentication.userAuth.domain;

import org.library.thelibraryj.authentication.userAuth.dto.UserCreationRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoWithImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface UserAuthMapper {
    UserAuth userAuthRequestToUserAuth(UserCreationRequest userCreationRequest);

    @Mapping(source = "userInfoResponse.username", target = "username")
    @Mapping(source = "userAuth.email", target = "email")
    UserCreationResponse userAuthAndUserInfoResponseToUserCreationResponse(UserInfoWithImageResponse userInfoResponse, UserAuth userAuth);
}
