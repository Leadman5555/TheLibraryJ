package org.library.thelibraryj.authentication.userAuth.domain;

import org.library.thelibraryj.authentication.userAuth.dto.UserCreationRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserAuthResponse;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface UserAuthMapper {
    UserAuth userAuthRequestToUserAuth(UserCreationRequest userCreationRequest);

    UserAuthResponse userAuthToUserAuthResponse(UserAuth userAuth);

    @Mapping(source = "userInfoResponse.username", target = "username")
    @Mapping(source = "userAuth.email", target = "email")
    @Mapping(source = "userAuth.id", target = "userAuthId")
    UserCreationResponse userAuthAndUserInfoResponseToUserCreationResponse(UserInfoResponse userInfoResponse, UserAuth userAuth);
}
