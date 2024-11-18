package org.library.thelibraryj.userInfo;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userInfo.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public interface UserInfoService {
    boolean existsById(UUID userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Either<GeneralError, UserInfoWithImageResponse> getUserInfoResponseById(UUID userId);
    Either<GeneralError, UserInfoWithImageResponse> getUserInfoResponseByUsername(String username);
    Either<GeneralError, UUID> getUserInfoIdByEmail(String email);
    Either<GeneralError, RatingUpsertData> getUsernameAndIdByEmail(String email);
    Either<GeneralError, BookCreationUserData> getAndValidateAuthorData(String authorEmail);
    UserInfoWithImageResponse createUserInfoWithImage(UserInfoRequest userInfoRequest, MultipartFile imageFile);
    UserInfoResponse createUserInfo(UserInfoRequest userInfoRequest);
    Either<GeneralError, UserInfoResponse> forceUpdateRank(UserInfoRankUpdateRequest userInfoRankUpdateRequest);
    Either<GeneralError, UserInfoResponse> updateRank(UUID userId);
    Either<GeneralError, UserInfoResponse> updateUserInfoUsername(UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest);
    Either<GeneralError, UserInfoWithImageResponse> updateProfileImage(UserInfoImageUpdateRequest userInfoImageUpdateRequest) throws IOException;
    void updateRatingScore(UserInfoScoreUpdateRequest userInfoScoreUpdateRequest);
}
