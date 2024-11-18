package org.library.thelibraryj.userInfo.domain;

import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoWithImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface UserInfoMapper {
    @Mapping(source = "id", target = "userId")
    UserInfoResponse userInfoToUserInfoResponse(UserInfo userInfo);
    @Mapping(source = "userInfo.id", target = "userId")
    UserInfoWithImageResponse userInfoToUserInfoWithImageResponse(UserInfo userInfo, byte[] profileImage);

    UserInfo userInfoRequestToUserInfo(UserInfoRequest userInfoRequest);
}
