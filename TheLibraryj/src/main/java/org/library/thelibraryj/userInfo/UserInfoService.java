package org.library.thelibraryj.userInfo;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userInfo.domain.BookCreationUserView;
import org.library.thelibraryj.userInfo.domain.RatingUpsertView;
import org.library.thelibraryj.userInfo.domain.UserInfoDetailsView;
import org.library.thelibraryj.userInfo.dto.UserInfoImageUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoMiniResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoPreferenceUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoScoreUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoStatusUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoUsernameUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoWithImageResponse;
import org.library.thelibraryj.userInfo.dto.UserProfileResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public interface UserInfoService {
    boolean existsById(UUID userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Either<GeneralError, UserProfileResponse> getUserProfileById(UUID userId);
    Either<GeneralError, UserProfileResponse> getUserProfileByUsername(String username);
    Either<GeneralError, UserProfileResponse> getUserProfileByEmail(String email);
    UserInfoMiniResponse getUserInfoMiniResponseByEmail(String email);
    UserInfoDetailsView getUserInfoDetailsByUsername(String username);
    Either<GeneralError, UUID> getUserInfoIdByEmail(String email);
    RatingUpsertView getUsernameAndIdByEmail(String email);
    Either<GeneralError, BookCreationUserView> getAndValidateAuthorData(String authorEmail);
    UserInfoWithImageResponse createUserInfoWithImage(UserInfoRequest userInfoRequest, MultipartFile imageFile);
    UserInfoResponse createUserInfo(UserInfoRequest userInfoRequest);
    Either<GeneralError, UserInfoResponse> forceUpdateRank(UserInfoRankUpdateRequest userInfoRankUpdateRequest);
    Either<GeneralError, UserInfoResponse> updateRank(UUID userId);
    Either<GeneralError, UserInfoResponse> updateUserInfoUsername(UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest);
    Either<GeneralError, UserInfoWithImageResponse> updateProfileImage(UserInfoImageUpdateRequest userInfoImageUpdateRequest) throws IOException;
    Either<GeneralError, UserInfoResponse> updateUserInfoStatus(UserInfoStatusUpdateRequest userInfoStatusUpdateRequest);
    Either<GeneralError, UserInfoResponse> updateUserInfoPreference(UserInfoPreferenceUpdateRequest userInfoPreferenceUpdateRequest);
    @Async
    void updateRatingScore(UserInfoScoreUpdateRequest userInfoScoreUpdateRequest);
}
