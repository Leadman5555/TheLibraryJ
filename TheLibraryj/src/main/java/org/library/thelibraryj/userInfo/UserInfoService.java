package org.library.thelibraryj.userInfo;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userInfo.domain.BookCreationUserView;
import org.library.thelibraryj.userInfo.domain.RatingUpsertView;
import org.library.thelibraryj.userInfo.domain.UserInfoDetailsView;
import org.library.thelibraryj.userInfo.dto.request.*;
import org.library.thelibraryj.userInfo.dto.response.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public interface UserInfoService {
    @Async
    void updateRatingScore(UserInfoScoreUpdateRequest userInfoScoreUpdateRequest);
    boolean existsById(UUID userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean checkWritingEligibility(String forUserEmail);
    Either<GeneralError, UserProfileResponse> getUserProfileById(UUID userId);
    Either<GeneralError, UserProfileResponse> getUserProfileByUsername(String username);
    Either<GeneralError, UserProfileResponse> getUserProfileByEmail(String email);
    Either<GeneralError, UserInfoMiniResponse> getUserInfoMiniResponseByEmail(String email);
    Either<GeneralError, UserInfoDetailsView> getUserInfoDetailsByUsername(String username);
    Either<GeneralError, UUID> getUserInfoIdByEmail(String email);
    Either<GeneralError, RatingUpsertView>  getUsernameAndIdByEmail(String email);
    Either<GeneralError, BookCreationUserView> getAndValidateAuthorData(String authorEmail);
    UserInfoWithImageResponse createUserInfoWithImage(UserInfoRequest userInfoRequest, MultipartFile imageFile);
    @Async
    void createUserInfo(UserInfoRequest userInfoRequest);
    Either<GeneralError, UserRankUpdateResponse> forceUpdateRank(UserInfoRankUpdateRequest userInfoRankUpdateRequest);
    Either<GeneralError, UserRankUpdateResponse> updateRank(String forUserEmail);
    Either<GeneralError, UserUsernameUpdateResponse> updateUserInfoUsername(UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest);
    Either<GeneralError, UserProfileImageUpdateResponse> updateProfileImage(UserInfoImageUpdateRequest userInfoImageUpdateRequest) throws IOException;
    Either<GeneralError, UserStatusUpdateResponse> updateUserInfoStatus(UserInfoStatusUpdateRequest userInfoStatusUpdateRequest);
    Either<GeneralError, UserPreferenceUpdateResponse> updateUserInfoPreference(UserInfoPreferenceUpdateRequest userInfoPreferenceUpdateRequest);
}
