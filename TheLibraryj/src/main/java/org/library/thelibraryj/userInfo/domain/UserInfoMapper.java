package org.library.thelibraryj.userInfo.domain;

import org.library.thelibraryj.userInfo.dto.request.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.response.UserInfoResponse;
import org.library.thelibraryj.userInfo.dto.response.UserInfoWithImageResponse;
import org.library.thelibraryj.userInfo.dto.response.UserProfileResponse;
import org.library.thelibraryj.userInfo.dto.response.UserTopRankerResponse;
import org.library.thelibraryj.userInfo.dto.response.UserUsernameUpdateResponse;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
interface UserInfoMapper {
    UserInfoResponse userInfoToUserInfoResponse(UserInfo userInfo);
    UserInfoWithImageResponse userInfoToUserInfoWithImageResponse(UserInfo userInfo, String profileImageUrl);
    UserProfileResponse userInfoToUserProfileResponse(UserInfo userInfo, String profileImageUrl);

    UserInfo userInfoRequestToUserInfo(UserInfoRequest userInfoRequest);

    UserTopRankerResponse userInfoRankViewToUserTopRankerResponse(UserInfoRankView userInfoRankView, String profileImageUrl);

    UserUsernameUpdateResponse dataToUserUsernameUpdateResponse(String newUsername, Instant dataUpdatedAt);
    default LocalDateTime map(Instant value){
        return LocalDateTime.ofInstant(value, ZoneOffset.ofHours(1));
    }
}
