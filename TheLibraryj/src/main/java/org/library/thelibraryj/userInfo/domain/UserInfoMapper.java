package org.library.thelibraryj.userInfo.domain;

import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoWithImageResponse;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
interface UserInfoMapper {
    UserInfoResponse userInfoToUserInfoResponse(UserInfo userInfo);
    UserInfoWithImageResponse userInfoToUserInfoWithImageResponse(UserInfo userInfo, byte[] profileImage);

    UserInfo userInfoRequestToUserInfo(UserInfoRequest userInfoRequest);

    default LocalDateTime map(Instant value){
        return LocalDateTime.ofInstant(value, ZoneOffset.ofHours(1));
    }
}
