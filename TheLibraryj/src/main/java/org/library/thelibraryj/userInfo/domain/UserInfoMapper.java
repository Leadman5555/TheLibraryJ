package org.library.thelibraryj.userInfo.domain;

import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface UserInfoMapper {
    @Mapping(source = "id", target = "userId")
    UserInfoResponse userInfoToUserInfoResponse(UserInfo userInfo);
    UserInfo userInfoRequestToUserInfo(UserInfoRequest userInfoRequest);
}
