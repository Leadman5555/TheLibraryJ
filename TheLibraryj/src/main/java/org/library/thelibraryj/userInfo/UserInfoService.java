package org.library.thelibraryj.userInfo;

import io.vavr.control.Either;
import org.library.thelibraryj.book.dto.bookDto.response.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userInfo.domain.BookCreationUserView;
import org.library.thelibraryj.userInfo.domain.RatingUpsertView;
import org.library.thelibraryj.userInfo.domain.UserInfoDetailsView;
import org.library.thelibraryj.userInfo.dto.request.FavouriteBookMergerRequest;
import org.library.thelibraryj.userInfo.dto.request.FavouriteBookRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoImageUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoPreferenceUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoScoreUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoStatusUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoUsernameUpdateRequest;
import org.library.thelibraryj.userInfo.dto.response.FavouriteBookMergerResponse;
import org.library.thelibraryj.userInfo.dto.response.UserInfoMiniResponse;
import org.library.thelibraryj.userInfo.dto.response.UserInfoWithImageResponse;
import org.library.thelibraryj.userInfo.dto.response.UserPreferenceUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserProfileImageUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserProfileResponse;
import org.library.thelibraryj.userInfo.dto.response.UserRankUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserStatusUpdateResponse;
import org.library.thelibraryj.userInfo.dto.response.UserTopRankerResponse;
import org.library.thelibraryj.userInfo.dto.response.UserUsernameUpdateResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
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
    Either<GeneralError, Set<BookPreviewResponse>> getFavouriteBooks(String email);
    Either<GeneralError, Set<UUID>> getFavouriteBooksIds(String email);
    Either<GeneralError, Integer> addBookToFavourites(FavouriteBookRequest favouriteBookRequest);
    @Async
    void removeBookFromFavourites(FavouriteBookRequest favouriteBookRequest);
    @Async
    void removeBookFromFavouritesForAllUsers(UUID bookId);
    Either<GeneralError, FavouriteBookMergerResponse> mergeFavouriteBooks(FavouriteBookMergerRequest mergerRequest);
    List<UserTopRankerResponse> getTopUsers();
}
