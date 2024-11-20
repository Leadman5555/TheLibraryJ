package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.userInfo.dto.BookCreationUserData;
import org.library.thelibraryj.userInfo.dto.RatingUpsertData;
import org.library.thelibraryj.userInfo.dto.UserInfoImageUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoScoreUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoUsernameUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoWithImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

@Service
@Transactional
class UserInfoServiceImpl implements org.library.thelibraryj.userInfo.UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper userInfoMapper;
    private final UserInfoConfig userInfoConfig;
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

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, UserInfoMapper userInfoMapper, UserInfoConfig config, UserInfoImageHandler userInfoImageHandler) {
        this.userInfoRepository = userInfoRepository;
        this.userInfoMapper = userInfoMapper;
        userInfoConfig = config;
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
    public Either<GeneralError, UserInfoWithImageResponse> getUserInfoResponseById(UUID userId) {
        Either<GeneralError, UserInfo> fetched = getUserInfoById(userId);
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        return Either.right(userInfoMapper.userInfoToUserInfoWithImageResponse(fetched.get(), userInfoImageHandler.fetchProfileImage(userId)));
    }

    @Override
    public Either<GeneralError, UserInfoWithImageResponse> getUserInfoResponseByUsername(String username) {
        Either<GeneralError, UserInfo> fetched = Try.of(() -> userInfoRepository.getByUsername(username))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundUsername(username)));
        if(fetched.isLeft()) return Either.left(fetched.getLeft());
        return Either.right(userInfoMapper.userInfoToUserInfoWithImageResponse(fetched.get(), userInfoImageHandler.fetchProfileImage(fetched.get().getId())));
    }

    @Override
    public Either<GeneralError, UUID> getUserInfoIdByEmail(String email) {
        return Try.of(() -> userInfoRepository.getIdByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundUsername(email)));
    }

    Either<GeneralError, UserInfo> getUserInfoByEmail(String email){
        return Try.of(() -> userInfoRepository.getByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFoundUsername(email)));
    }

    @Override
    public Either<GeneralError, RatingUpsertData> getUsernameAndIdByEmail(String email) {
        Either<GeneralError, Object> fetched = Try.of(() -> userInfoRepository.getRatingUpsertData(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new UserInfoError.UserInfoEntityNotFound(email)));
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        Object[] values = (Object[]) fetched.get();
        return Either.right(new RatingUpsertData((UUID) values[0], (String) values[1]));
    }

    @Override
    public Either<GeneralError, BookCreationUserData> getAndValidateAuthorData(String authorEmail) {
        Either<GeneralError, Object> fetched = Try.of(() -> userInfoRepository.getBookCreationUserData(authorEmail))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new UserInfoError.UserInfoEntityNotFound(authorEmail)));
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        Object[] values = (Object[]) fetched.get();
        long ageDiff = ChronoUnit.HOURS.between((Instant) values[2], Instant.now());
        if (ageDiff < userInfoConfig.getMinimal_age_hours())
            return Either.left(new UserInfoError.UserAccountTooYoung(authorEmail, ageDiff));
        return Either.right(new BookCreationUserData((UUID) values[0], (String) values[1]));
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
                created.getDataUpdatedAt(),
                userInfoImageHandler.fetchProfileImage(created.getId())
        );
    }

    @Override
    public UserInfoResponse createUserInfo(UserInfoRequest userInfoRequest) {
        return userInfoMapper.userInfoToUserInfoResponse(createUserInfoInternal(userInfoRequest));
    }

    @Transactional
    UserInfo createUserInfoInternal(UserInfoRequest userInfoRequest){
        UserInfo mapped = userInfoMapper.userInfoRequestToUserInfo(userInfoRequest);
        mapped.setRank(0);
        mapped.setDataUpdatedAt(Instant.now());
        UserInfo saved = userInfoRepository.persist(mapped);
        userInfoRepository.flush();
        return saved;
    }

    @Transactional
    @Override
    public Either<GeneralError, UserInfoResponse> forceUpdateRank(UserInfoRankUpdateRequest userInfoRankUpdateRequest) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoRankUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        int newRank = fetched.getRank() + userInfoRankUpdateRequest.rankChange();
        fetched.setRank(
                max(min(newRank, rank_requirements.length), 0)
        );
        userInfoRepository.update(fetched);
        return Either.right(userInfoMapper.userInfoToUserInfoResponse(fetched));
    }

    @Transactional
    @Override
    public Either<GeneralError, UserInfoResponse> updateRank(UUID userId) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoById(userId);
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        int newRank = fetched.getRank();
        int currentPoints = fetched.getCurrentScore();

        while(newRank < rank_requirements.length && currentPoints - rank_requirements[newRank] >=0){
            currentPoints -= rank_requirements[newRank];
            newRank++;
        }
        if(newRank == fetched.getRank()) return Either.left(new UserInfoError.UserNotEligibleForRankIncrease(fetched.getEmail(), currentPoints - rank_requirements[newRank]));
        fetched.setRank(newRank);
        fetched.setCurrentScore(currentPoints);
        userInfoRepository.update(fetched);
        return Either.right(userInfoMapper.userInfoToUserInfoResponse(fetched));
    }

    @Transactional
    @Override
    public Either<GeneralError, UserInfoResponse> updateUserInfoUsername(UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest) {
        if (existsByUsername(userInfoUsernameUpdateRequest.username()))
            return Either.left(new UserInfoError.UsernameNotUnique());
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoUsernameUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        long cooldownDiff = ChronoUnit.DAYS.between(fetched.getDataUpdatedAt(), Instant.now());
        if (cooldownDiff < userInfoConfig.getUsername_change_cooldown_days())
            return Either.left(new UserInfoError.UsernameUpdateCooldown(userInfoConfig.getUsername_change_cooldown_days() - cooldownDiff));
        fetched.setUsername(userInfoUsernameUpdateRequest.username());
        fetched.setDataUpdatedAt(Instant.now());
        userInfoRepository.update(fetched);
        bookService.updateAllForNewUsername(fetched.getId(), userInfoUsernameUpdateRequest.username());
        return Either.right(userInfoMapper.userInfoToUserInfoResponse(fetched));
    }

    @Override
    public Either<GeneralError, UserInfoWithImageResponse> updateProfileImage(UserInfoImageUpdateRequest userInfoImageUpdateRequest) throws IOException {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoByEmail(userInfoImageUpdateRequest.email());
        if (fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        if (userInfoImageHandler.upsertProfileImageImage(fetchedE.get().getId(), userInfoImageUpdateRequest.newImage()))
            return Either.left(new UserInfoError.ProfileImageUpdateFailed());
        return Either.right(userInfoMapper.userInfoToUserInfoWithImageResponse(fetchedE.get(), userInfoImageUpdateRequest.newImage().getBytes()));
    }

    @Transactional
    @Override
    public void updateRatingScore(UserInfoScoreUpdateRequest userInfoScoreUpdateRequest) {
        Either<GeneralError, UserInfo> user = getUserInfoById(userInfoScoreUpdateRequest.forUser());
        if(user.isRight()){
            if(userInfoScoreUpdateRequest.hadComment()) user.get().incrementScore(points_for_comment + points_for_review);
            else user.get().incrementScore(points_for_review);
            userInfoRepository.update(user.get());
        }
        Either<GeneralError, UserInfo> author = getUserInfoById(userInfoScoreUpdateRequest.forAuthor());
        if(author.isRight()){
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
}
