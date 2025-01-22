package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.userInfo.dto.request.UserInfoImageUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoPreferenceUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoScoreUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoStatusUpdateRequest;
import org.library.thelibraryj.userInfo.dto.request.UserInfoUsernameUpdateRequest;
import org.library.thelibraryj.userInfo.dto.response.UserInfoMiniResponse;
import org.library.thelibraryj.userInfo.dto.response.UserInfoResponse;
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
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    private final static int points_for_comment = 1;
    private final static int points_for_review = 1;
    private final static int points_for_author = 3;
    private final static int[] rank_requirements = {3, 5, 10, 20, 40, 60, 100, 200, 500, 1000};

    @Autowired
    void setBookService(@Lazy BookService bookService) {
        this.bookService = bookService;
    }

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, UserInfoMapper userInfoMapper, UserInfoProperties properties, UserInfoImageHandler userInfoImageHandler) {
        this.userInfoRepository = userInfoRepository;
        this.userInfoMapper = userInfoMapper;
        userInfoProperties = properties;
        this.userInfoImageHandler = userInfoImageHandler;
    }

    @Override
    public boolean existsById(UUID userId) {
        return userInfoRepository.existsById(userId);
    }

    Either<GeneralError, UserInfo> getUserInfoById(UUID userId) {
        return Try.of(() -> userInfoRepository.findById(userId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundById()));
    }

    @Override
    public Either<GeneralError, UserProfileResponse> getUserProfileById(UUID userId) {
        Either<GeneralError, UserInfo> fetched = getUserInfoById(userId);
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        return Either.right(userInfoMapper.userInfoToUserProfileResponse(fetched.get(), userInfoImageHandler.fetchProfileImage(userId)));
    }

    @Override
    public Either<GeneralError, UserProfileResponse> getUserProfileByUsername(String username) {
        Either<GeneralError, UserInfo> fetched = Try.of(() -> userInfoRepository.getByUsername(username))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundUsername(username)));
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        return Either.right(userInfoMapper.userInfoToUserProfileResponse(fetched.get(), userInfoImageHandler.fetchProfileImage(fetched.get().getId())));
    }

    @Override
    public Either<GeneralError, UserProfileResponse> getUserProfileByEmail(String email) {
        Either<GeneralError, UserInfo> fetched = Try.of(() -> userInfoRepository.getByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFound(email)));
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        return Either.right(userInfoMapper.userInfoToUserProfileResponse(fetched.get(), userInfoImageHandler.fetchProfileImage(fetched.get().getId())));

    }

    @Override
    public UserInfoMiniResponse getUserInfoMiniResponseByEmail(String email) {
        UserInfoMiniView fetched = userInfoRepository.getUserInfoMiniView(email);
        return new UserInfoMiniResponse(fetched.getUsername(), userInfoImageHandler.fetchProfileImage(fetched.getId()));
    }

    @Override
    public UserInfoDetailsView getUserInfoDetailsByUsername(String username) {
        return userInfoRepository.getUserInfoDetailsView(username);
    }

    @Override
    public Either<GeneralError, UUID> getUserInfoIdByEmail(String email) {
        return Try.of(() -> userInfoRepository.getIdByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundUsername(email)));
    }

    Either<GeneralError, UserInfo> getUserInfoByEmail(String email) {
        return Try.of(() -> userInfoRepository.getByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundUsername(email)));
    }

    @Override
    public RatingUpsertView getUsernameAndIdByEmail(String email) {
        return userInfoRepository.getRatingUpsertView(email);
    }

    @Override
    public Either<GeneralError, BookCreationUserView> getAndValidateAuthorData(String authorEmail) {
        BookCreationUserView fetched = userInfoRepository.getBookCreationUserView(authorEmail);
        long ageDiff = ChronoUnit.HOURS.between(fetched.getCreatedAt(), Instant.now());
        if (ageDiff < userInfoProperties.getMinimal_age_hours())
            return Either.left(new UserInfoError.UserAccountTooYoung(authorEmail, ageDiff));
        return Either.right(fetched);
    }

    @Transactional
    @Override
    public UserInfoWithImageResponse createUserInfoWithImage(UserInfoRequest userInfoRequest, @Nullable MultipartFile profileImage) {
        UserInfo created = createUserInfoInternal(userInfoRequest);
        if (profileImage != null) userInfoImageHandler.upsertProfileImageImage(created.getId(), profileImage);
        return new UserInfoWithImageResponse(
                created.getUsername(),
                created.getEmail(),
                created.getRank(),
                created.getCurrentScore(),
                created.getStatus(),
                created.getPreference(),
                userInfoImageHandler.fetchProfileImage(created.getId())
        );
    }

    @Override
    public UserInfoResponse createUserInfo(UserInfoRequest userInfoRequest) {
        return userInfoMapper.userInfoToUserInfoResponse(createUserInfoInternal(userInfoRequest));
    }

    @Transactional
    UserInfo createUserInfoInternal(UserInfoRequest userInfoRequest) {
        UserInfo mapped = userInfoMapper.userInfoRequestToUserInfo(userInfoRequest);
        mapped.setUsername(escapeHtml(mapped.getUsername()));
        mapped.setRank(0);
        mapped.setCurrentScore(0);
        mapped.setPreference((short) 0);
        mapped.setDataUpdatedAt(Instant.now());
        UserInfo saved = userInfoRepository.persist(mapped);
        userInfoRepository.flush();
        return saved;
    }

    @Transactional
    @Override
    public Either<GeneralError, UserRankUpdateResponse> forceUpdateRank(UserInfoRankUpdateRequest userInfoRankUpdateRequest) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoRankUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        int newRank = max(min(fetched.getRank() + userInfoRankUpdateRequest.rankChange(), rank_requirements.length), 0);
        if(newRank < fetched.getRank() && fetched.getPreference() > newRank/10) fetched.setPreference((short) 0);
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

        while (newRank < rank_requirements.length && currentPoints - rank_requirements[newRank] >= 0) {
            currentPoints -= rank_requirements[newRank];
            newRank++;
        }
        if (newRank == fetched.getRank())
            return Either.left(new UserInfoError.UserNotEligibleForRankIncrease(fetched.getEmail(), currentPoints - rank_requirements[newRank]));
        fetched.setRank(newRank);
        fetched.setCurrentScore(currentPoints);
        userInfoRepository.update(fetched);
        return Either.right(new UserRankUpdateResponse(newRank, currentPoints, fetched.getPreference()));
    }

    @Transactional
    @Override
    public Either<GeneralError, UserUsernameUpdateResponse> updateUserInfoUsername(UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest) {
        if (existsByUsername(userInfoUsernameUpdateRequest.username()))
            return Either.left(new UserInfoError.UsernameNotUnique());
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoUsernameUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        long cooldownDiff = ChronoUnit.DAYS.between(fetched.getDataUpdatedAt(), Instant.now());
        if (cooldownDiff < userInfoProperties.getUsername_change_cooldown_days())
            return Either.left(new UserInfoError.UsernameUpdateCooldown(userInfoProperties.getUsername_change_cooldown_days() - cooldownDiff, fetched.getEmail()));
        final String escapedUsername = escapeHtml(userInfoUsernameUpdateRequest.username());
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
                return Either.left(new UserInfoError.ProfileImageUpdateFailed());
            return Either.right(new UserProfileImageUpdateResponse(userInfoImageHandler.getDefaultImage()));
        } else {
            if (!userInfoImageHandler.upsertProfileImageImage(fetchedE.get(), userInfoImageUpdateRequest.newImage()))
                return Either.left(new UserInfoError.ProfileImageUpdateFailed());
            return Either.right(new UserProfileImageUpdateResponse(userInfoImageUpdateRequest.newImage().getBytes()));
        }
    }

    @Transactional
    @Override
    public Either<GeneralError, UserStatusUpdateResponse> updateUserInfoStatus(UserInfoStatusUpdateRequest userInfoStatusUpdateRequest) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoStatusUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        String escapedStatus = escapeHtml(userInfoStatusUpdateRequest.status());
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

    @Async
    @Transactional
    @Override
    public void updateRatingScore(UserInfoScoreUpdateRequest userInfoScoreUpdateRequest) {
        Either<GeneralError, UserInfo> user = getUserInfoById(userInfoScoreUpdateRequest.forUser());
        if (user.isRight()) {
            if (userInfoScoreUpdateRequest.hadComment())
                user.get().incrementScore(points_for_comment + points_for_review);
            else user.get().incrementScore(points_for_review);
            userInfoRepository.update(user.get());
        }
        Either<GeneralError, UserInfo> author = getUserInfoById(userInfoScoreUpdateRequest.forAuthor());
        if (author.isRight()) {
            author.get().incrementScore(points_for_author);
            userInfoRepository.update(author.get());
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userInfoRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userInfoRepository.existsByEmail(email);
    }

    private static String escapeHtml(String toEscape) {
        return HtmlUtils.htmlEscape(toEscape)
                .replace("&#39;", "'").replace("&quot;", "\"");
    }
}
