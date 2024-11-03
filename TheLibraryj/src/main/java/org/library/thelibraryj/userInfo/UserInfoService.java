package org.library.thelibraryj.userInfo;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userInfo.dto.UserInfoUsernameUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserInfoService {
    boolean existsById(UUID userId);
    boolean existsByUsername(String username);
    Either<GeneralError, UserInfoResponse> getUserInfoResponseById(UUID userId);
    Either<GeneralError, String> getAuthorUsernameAndCheckAccountAge(UUID userId);
    UserInfoResponse createUserInfo(UserInfoRequest userInfoRequest);
    Either<GeneralError, UserInfoResponse> updateRank(UserInfoRankUpdateRequest userInfoRankUpdateRequest);
    Either<GeneralError, UserInfoResponse> updateUserInfoUsername(UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest);
}
