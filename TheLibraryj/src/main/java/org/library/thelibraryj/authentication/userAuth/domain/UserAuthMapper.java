package org.library.thelibraryj.authentication.userAuth.domain;

import org.library.thelibraryj.authentication.userAuth.dto.UserAuthRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserAuthResponse;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface UserAuthMapper {
    UserAuth userAuthRequestToUserAuth(UserAuthRequest userAuthRequest);
    UserAuthResponse userAuthToUserAuthResponse(UserAuth userAuth);
    @Mapping(source = "userAuth.email", target = "email")
    @Mapping(source = "userInfoResponse.username", target = "username")
    @Mapping(source = "userInfoResponse.userId", target = "userId")
    //  @Mapping(source = "userAuth.isEnabled", target = "isEnabled")
    UserCreationResponse userAuthAndUserInfoResponseToUserCreationResponse(UserAuth userAuth, UserInfoResponse userInfoResponse);
}
