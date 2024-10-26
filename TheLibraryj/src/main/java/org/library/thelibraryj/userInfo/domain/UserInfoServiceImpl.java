package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.book.BookService;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.userInfo.dto.UserInfoRankUpdateRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.library.thelibraryj.userInfo.dto.UserInfoUsernameUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

@Service
@Transactional
class UserInfoServiceImpl implements org.library.thelibraryj.userInfo.UserInfoService{

    private final UserInfoRepository userInfoRepository;
    private final UserInfoMapper userInfoMapper;
    private final UserInfoConfig userInfoConfig;
    private BookService bookService;
    private ApplicationContext context;

    @Autowired
    void setBookService(@Lazy BookService bookService) {
        this.bookService = bookService;
    }

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, UserInfoMapper userInfoMapper, UserInfoConfig config) {
        this.userInfoRepository = userInfoRepository;
        this.userInfoMapper = userInfoMapper;
        userInfoConfig = config;
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
                .flatMap(e -> e.toEither(new UserInfoError.UserInfoEntityNotFound(userId)));
    }

    @Override
    public Either<GeneralError, UserInfoResponse> getUserInfoResponseById(UUID userId) {
        Either<GeneralError, UserInfo> fetched = getUserInfoById(userId);
        if(fetched.isLeft()) return Either.left(fetched.getLeft());
        return Either.right(userInfoMapper.userInfoToUserInfoResponse(fetched.get()));
    }

    @Override
    public Either<GeneralError, String> getAuthorUsernameAndCheckAccountAge(UUID userId) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoById(userId);
        if(fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        long ageDiff = ChronoUnit.HOURS.between(fetched.getCreatedAt(), Instant.now());
        if(ageDiff < userInfoConfig.getMinimal_age_hours())
            return Either.left(new UserInfoError.UserAccountTooYoung(userId, ageDiff));
        return Either.right(fetched.getUsername());
    }

    @Transactional
    @Override
    public Either<GeneralError, UserInfoResponse> createUserInfo(UserInfoRequest userInfoRequest) {
        //unique username/email verification moved to userAuth
        UserInfo mapped = userInfoMapper.userInfoRequestToUserInfo(userInfoRequest);
        mapped.setRank(0);
        mapped.setDataUpdatedAt(Instant.now());
        userInfoRepository.persist(mapped);
        return Either.right(userInfoMapper.userInfoToUserInfoResponse(mapped));
    }

    @Transactional
    @Override
    public Either<GeneralError, UserInfoResponse> updateRank(UserInfoRankUpdateRequest userInfoRankUpdateRequest) {
        Either<GeneralError, UserInfo> fetchedE = getUserInfoById(userInfoRankUpdateRequest.userId());
        if(fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        int newRank = fetched.getRank() + userInfoRankUpdateRequest.rankChange();
        fetched.setRank(
                max(min(newRank, 10), 0)
        );
        userInfoRepository.update(fetched);
        return Either.right(userInfoMapper.userInfoToUserInfoResponse(fetched));
    }

    @Transactional
    @Override
    public Either<GeneralError, UserInfoResponse> updateUserInfoUsername(UserInfoUsernameUpdateRequest userInfoUsernameUpdateRequest) {
        if(existsByUsername(userInfoUsernameUpdateRequest.username()))
            return Either.left(new UserInfoError.UsernameNotUnique());
        Either<GeneralError, UserInfo> fetchedE = getUserInfoById(userInfoUsernameUpdateRequest.userId());
        if(fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        UserInfo fetched = fetchedE.get();
        long cooldownDiff = ChronoUnit.DAYS.between(fetched.getDataUpdatedAt(), Instant.now());
        if(cooldownDiff < userInfoConfig.getUsername_change_cooldown_days())
            return Either.left(new UserInfoError.UsernameUpdateCooldown(userInfoConfig.getUsername_change_cooldown_days() - cooldownDiff));
        fetched.setUsername(userInfoUsernameUpdateRequest.username());
        fetched.setDataUpdatedAt(Instant.now());
        userInfoRepository.update(fetched);
        bookService.updateAuthorUsername(userInfoUsernameUpdateRequest.userId(), userInfoUsernameUpdateRequest.username());
        return Either.right(userInfoMapper.userInfoToUserInfoResponse(fetched));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userInfoRepository.existsByUsername(username);
    }
}
