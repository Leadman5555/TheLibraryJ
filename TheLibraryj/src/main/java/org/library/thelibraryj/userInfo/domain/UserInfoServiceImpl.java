package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.book.dto.bookDto.BookPreviewResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.infrastructure.textParsers.inputParsers.HtmlEscaper;
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
import org.library.thelibraryj.userInfo.dto.response.UserUsernameUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

@Service
@Transactional(readOnly = true)
class UserInfoServiceImpl implements org.library.thelibraryj.userInfo.UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper userInfoMapper;
    private final UserInfoProperties userInfoProperties;
    private final UserInfoImageHandler userInfoImageHandler;
    private BookService bookService;
    private final int[] rankRequirementsArray;
    private final HtmlEscaper htmlEscaper;

    @Autowired
    void setBookService(@Lazy BookService bookService) {
        this.bookService = bookService;
    }

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, UserInfoMapper userInfoMapper, UserInfoProperties properties, UserInfoImageHandler userInfoImageHandler, HtmlEscaper htmlEscaper) {
        this.userInfoRepository = userInfoRepository;
        this.userInfoMapper = userInfoMapper;
        userInfoProperties = properties;
        this.htmlEscaper = htmlEscaper;
        this.userInfoImageHandler = userInfoImageHandler;
        rankRequirementsArray = Arrays.stream(properties.getRank_requirements().split(","))
                .map(String::trim).mapToInt(Integer::parseInt).toArray();
    }

    @Override
    public boolean existsById(UUID userId) {
        return userInfoRepository.existsById(userId);
    }

    Either<GeneralError, UserInfo> getUserInfoLazyById(UUID userId) {
        return Try.of(() -> userInfoRepository.findById(userId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundById(userId)));
    }

    Either<GeneralError, UserInfo> getUserInfoEagerById(UUID userId) {
        return Try.of(() -> userInfoRepository.fetchUserInfoEagerById(userId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundById(userId)));
    }

    Either<GeneralError, UserInfo> getUserInfoEagerByEmail(String email) {
        return Try.of(() -> userInfoRepository.fetchUserInfoEagerByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundByEmail(email)));
    }

    @Override
    public Either<GeneralError, UserProfileResponse> getUserProfileById(UUID userId) {
        Either<GeneralError, UserInfo> fetched = getUserInfoLazyById(userId);
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        return Either.right(userInfoMapper.userInfoToUserProfileResponse(fetched.get(), userInfoImageHandler.fetchProfileImage(userId)));
    }

    @Override
    public Either<GeneralError, UserProfileResponse> getUserProfileByUsername(String username) {
        Either<GeneralError, UserInfo> fetched = Try.of(() -> userInfoRepository.getByUsername(username))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundByUsername(username)));
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        return Either.right(userInfoMapper.userInfoToUserProfileResponse(fetched.get(), userInfoImageHandler.fetchProfileImage(fetched.get().getId())));
    }

    @Override
    public Either<GeneralError, UserProfileResponse> getUserProfileByEmail(String email) {
        Either<GeneralError, UserInfo> fetched = Try.of(() -> userInfoRepository.getByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundByEmail(email)));
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        return Either.right(userInfoMapper.userInfoToUserProfileResponse(fetched.get(), userInfoImageHandler.fetchProfileImage(fetched.get().getId())));

    }

    @Override
    public Either<GeneralError, UserInfoMiniResponse> getUserInfoMiniResponseByEmail(String email) {
        return userInfoRepository.getUserInfoMiniView(email)
                .map(view ->
                        Either.<GeneralError, UserInfoMiniResponse>right(
                                new UserInfoMiniResponse(view.getUsername(),
                                        userInfoImageHandler.fetchProfileImage(view.getId())))
                )
                .orElse(Either.left(new UserInfoError.UserInfoEntityNotFoundByEmail(email)));

    }

    @Override
    public Either<GeneralError, UserInfoDetailsView> getUserInfoDetailsByUsername(String username) {
        return userInfoRepository.getUserInfoDetailsView(username).map(Either::<GeneralError, UserInfoDetailsView>right)
                .orElse(Either.left(new UserInfoError.UserInfoEntityNotFoundByUsername(username)));
    }

    @Override
    public Either<GeneralError, UUID> getUserInfoIdByEmail(String email) {
        return Try.of(() -> userInfoRepository.getIdByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundByUsername(email)));
    }

    Either<GeneralError, UserInfo> getUserInfoByEmail(String email) {
        return Try.of(() -> userInfoRepository.getByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundByUsername(email)));
    }

    @Override
    public Either<GeneralError, RatingUpsertView> getUsernameAndIdByEmail(String email) {
        return userInfoRepository.getRatingUpsertView(email).map(Either::<GeneralError, RatingUpsertView>right)
                .orElse(Either.left(new UserInfoError.UserInfoEntityNotFoundByEmail(email)));
    }

    @Override
    public Either<GeneralError, BookCreationUserView> getAndValidateAuthorData(String authorEmail) {
        return userInfoRepository.getBookCreationUserView(authorEmail).map(bookCreationUserView -> {
            long ageDiff = ChronoUnit.HOURS.between(bookCreationUserView.getCreatedAt(), Instant.now());
            if (ageDiff < userInfoProperties.getMinimal_age_hours())
                return Either.<GeneralError, BookCreationUserView>left(new UserInfoError.UserAccountTooYoung(authorEmail, ageDiff));
            return Either.<GeneralError, BookCreationUserView>right(bookCreationUserView);
        }).orElse(Either.left(new UserInfoError.UserInfoEntityNotFoundByEmail(authorEmail)));
    }

    @Override
    public boolean checkWritingEligibility(String forUserEmail) {
        return userInfoRepository.getCreatedAtByEmail(forUserEmail)
                .filter(
                        createdAt -> ChronoUnit.HOURS.between(createdAt, Instant.now()) >= userInfoProperties.getMinimal_age_hours()
                )
                .isPresent();
    }

    @Transactional
    @Override
    public UserInfoWithImageResponse createUserInfoWithImage(UserInfoRequest userInfoRequest, @Nullable MultipartFile profileImage) {
        UserInfo created = createUserInfoInternal(userInfoRequest);
        boolean customProfileImage = false;
        if (profileImage != null)
            customProfileImage = userInfoImageHandler.upsertProfileImageImage(created.getId(), profileImage);
        return new UserInfoWithImageResponse(
                created.getUsername(),
                created.getEmail(),
                created.getRank(),
                created.getCurrentScore(),
                created.getStatus(),
                created.getPreference(),
                customProfileImage ? userInfoImageHandler.fetchProfileImage(created.getId()) : userInfoImageHandler.getDefaultImage()
        );
    }

    @Async
    @Override
    @Transactional
    public void createUserInfo(UserInfoRequest userInfoRequest) {
        createUserInfoInternal(userInfoRequest);
    }

    @Transactional
    UserInfo createUserInfoInternal(UserInfoRequest userInfoRequest) {
        UserInfo mapped = userInfoMapper.userInfoRequestToUserInfo(userInfoRequest);
        mapped.setUsername(htmlEscaper.escapeHtml(mapped.getUsername()));
        mapped.setRank(0);
        mapped.setCurrentScore(0);
        mapped.setPreference((short) 0);
        mapped.setDataUpdatedAt(Instant.now());
        return userInfoRepository.persist(mapped);
    }

    @Transactional
    @Override
    public Either<GeneralError, UserRankUpdateResponse> forceUpdateRank(UserInfoRankUpdateRequest userInfoRankUpdateRequest) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoRankUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        int newRank = max(min(fetched.getRank() + userInfoRankUpdateRequest.rankChange(), rankRequirementsArray.length), 0);
        if (newRank < fetched.getRank() && fetched.getPreference() > newRank / 10) fetched.setPreference((short) 0);
        fetched.setRank(newRank);
        userInfoRepository.update(fetched);
        return Either.right(new UserRankUpdateResponse(newRank, fetched.getCurrentScore(), fetched.getPreference()));
    }

    @Transactional
    @Override
    public Either<GeneralError, UserRankUpdateResponse> updateRank(String forUserEmail) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(forUserEmail);
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        int newRank = fetched.getRank();
        int currentPoints = fetched.getCurrentScore();

        while (newRank < rankRequirementsArray.length && currentPoints - rankRequirementsArray[newRank] >= 0) {
            currentPoints -= rankRequirementsArray[newRank];
            newRank++;
        }
        if (newRank == fetched.getRank())
            return Either.left(new UserInfoError.UserNotEligibleForRankIncrease(fetched.getEmail(), currentPoints - rankRequirementsArray[newRank]));
        fetched.setRank(newRank);
        fetched.setCurrentScore(currentPoints);
        userInfoRepository.update(fetched);
        return Either.right(new UserRankUpdateResponse(newRank, currentPoints, fetched.getPreference()));
    }

    @Transactional
    @Override
    public Either<GeneralError, UserUsernameUpdateResponse> updateUserInfoUsername(UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest) {
        if (existsByUsername(userInfoUsernameUpdateRequest.username()))
            return Either.left(new UserInfoError.UsernameNotUnique(userInfoUsernameUpdateRequest.email()));
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoUsernameUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        long cooldownDiff = ChronoUnit.DAYS.between(fetched.getDataUpdatedAt(), Instant.now());
        if (cooldownDiff < userInfoProperties.getUsername_change_cooldown_days())
            return Either.left(new UserInfoError.UsernameUpdateCooldown(userInfoProperties.getUsername_change_cooldown_days() - cooldownDiff, fetched.getEmail()));
        final String escapedUsername = htmlEscaper.escapeHtml(userInfoUsernameUpdateRequest.username());
        fetched.setUsername(escapedUsername);
        fetched.setDataUpdatedAt(Instant.now());
        userInfoRepository.update(fetched);
        bookService.updateAllForNewUsername(fetched.getId(), escapedUsername);
        return Either.right(userInfoMapper.dataToUserUsernameUpdateResponse(escapedUsername, fetched.getDataUpdatedAt()));
    }

    @Transactional
    @Override
    public Either<GeneralError, UserProfileImageUpdateResponse> updateProfileImage(UserInfoImageUpdateRequest userInfoImageUpdateRequest) throws IOException {
        Either<GeneralError, UUID> fetchedE = getUserInfoIdByEmail(userInfoImageUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        if (userInfoImageUpdateRequest.newImage() == null) {
            if (!userInfoImageHandler.removeExistingProfileImage(fetchedE.get()))
                return Either.left(new UserInfoError.ProfileImageUpdateFailed(userInfoImageUpdateRequest.email()));
            return Either.right(new UserProfileImageUpdateResponse(userInfoImageHandler.getDefaultImage()));
        } else {
            if (!userInfoImageHandler.upsertProfileImageImage(fetchedE.get(), userInfoImageUpdateRequest.newImage()))
                return Either.left(new UserInfoError.ProfileImageUpdateFailed(userInfoImageUpdateRequest.email()));
            return Either.right(new UserProfileImageUpdateResponse(userInfoImageUpdateRequest.newImage().getBytes()));
        }
    }

    @Transactional
    @Override
    public Either<GeneralError, UserStatusUpdateResponse> updateUserInfoStatus(UserInfoStatusUpdateRequest userInfoStatusUpdateRequest) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoStatusUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        String escapedStatus = htmlEscaper.escapeHtml(userInfoStatusUpdateRequest.status());
        fetched.setStatus(escapedStatus);
        userInfoRepository.update(fetched);
        return Either.right(new UserStatusUpdateResponse(escapedStatus));
    }

    @Transactional
    @Override
    public Either<GeneralError, UserPreferenceUpdateResponse> updateUserInfoPreference(UserInfoPreferenceUpdateRequest userInfoPreferenceUpdateRequest) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoPreferenceUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        if (userInfoPreferenceUpdateRequest.preference() / 10 > fetched.getRank())
            return Either.left(new UserInfoError.UserNotEligibleForChosenPreference(userInfoPreferenceUpdateRequest.email(),
                    userInfoPreferenceUpdateRequest.preference() / 10 - fetched.getRank()));
        fetched.setPreference(userInfoPreferenceUpdateRequest.preference());
        userInfoRepository.update(fetched);
        return Either.right(new UserPreferenceUpdateResponse(userInfoPreferenceUpdateRequest.preference()));
    }

    @Override
    public Either<GeneralError, Set<BookPreviewResponse>> getFavouriteBooks(String email) {
        Either<GeneralError, UUID> fetchedE = getUserInfoIdByEmail(email);
        if(fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        return Either.right(bookService.getBookPreviewsByIds(userInfoRepository.fetchUserFavouriteBookIds(fetchedE.get())));
    }

    @Transactional
    @Override
    public Either<GeneralError, Integer> addBookToFavourites(FavouriteBookRequest favouriteBookRequest) {
        if(!bookService.checkIfBookExists(favouriteBookRequest.bookId()))
            return Either.left(new BookError.BookPreviewEntityNotFound(favouriteBookRequest.bookId().toString()));
        Either<GeneralError, UserInfo> fetchedE =  getUserInfoEagerByEmail(favouriteBookRequest.email());
        if(fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        int favouriteCount = fetched.addBookIdToFavourites(favouriteBookRequest.bookId());
        userInfoRepository.update(fetched);
        return Either.right(favouriteCount);
    }

    @Async
    @Transactional
    @Override
    public void removeBookFromFavourites(FavouriteBookRequest favouriteBookRequest) {
       getUserInfoIdByEmail(favouriteBookRequest.email()).peek(id -> userInfoRepository.removeBookFromFavourites(id, favouriteBookRequest.bookId()));
    }

    @Async
    @Transactional
    @Override
    public void removeBookFromFavouritesForAllUsers(UUID bookId) {
        userInfoRepository.removeBookFromFavouritesForAllUsers(bookId);
    }

    @Transactional
    @Override
    public Either<GeneralError, FavouriteBookMergerResponse> mergeAndFetchFavouriteBooks(FavouriteBookMergerRequest mergerRequest) {
        Either<GeneralError, UserInfo> fetchedFromE = getUserInfoEagerById(mergerRequest.fromUserId());
        if(fetchedFromE.isLeft()) return Either.left(fetchedFromE.getLeft());
        Either<GeneralError, UserInfo> fetchedToE = getUserInfoByEmail(mergerRequest.toUserEmail());
        if(fetchedToE.isLeft()) return Either.left(fetchedToE.getLeft());
        UserInfo fetchedTo = fetchedToE.get();
        UserInfo fetchedFrom = fetchedFromE.get();
        int sizeBefore = fetchedTo.getFavouriteBookIds().size();
        int sizeAfter = fetchedTo.addBookIdToFavourites(fetchedFrom.getFavouriteBookIds());
        userInfoRepository.update(fetchedTo);
        return Either.right(new FavouriteBookMergerResponse(
                sizeBefore,
                sizeAfter,
                fetchedFrom.getFavouriteBookIds().size(),
                fetchedFrom.getUsername(),
                fetchedTo.getUsername()
        ));
    }

    @Async
    @Transactional
    @Override
    public void updateRatingScore(UserInfoScoreUpdateRequest userInfoScoreUpdateRequest) {
        if (userInfoScoreUpdateRequest.hadComment())
            userInfoRepository.updateCurrentScore(userInfoScoreUpdateRequest.forUser(), userInfoProperties.getPoints_for_comment() + userInfoProperties.getPoints_for_review());
        else userInfoRepository.updateCurrentScore(userInfoScoreUpdateRequest.forUser(), userInfoProperties.getPoints_for_review());
        userInfoRepository.updateCurrentScore(userInfoScoreUpdateRequest.forAuthor(), userInfoProperties.getPoints_for_author());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userInfoRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userInfoRepository.existsByEmail(email);
    }
}
