package org.library.thelibraryj.userInfo.domain;

import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoWithImageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface UserInfoMapper {
    UserInfoResponse userInfoToUserInfoResponse(UserInfo userInfo);
    UserInfoWithImageResponse userInfoToUserInfoWithImageResponse(UserInfo userInfo, byte[] profileImage);

    UserInfo userInfoRequestToUserInfo(UserInfoRequest userInfoRequest);
}
