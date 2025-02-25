package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.request.FavouriteBookMergerRequest;
import org.library.thelibraryj.userInfo.dto.response.FavouriteBookMergerResponse;
import org.library.thelibraryj.userInfo.userInfoTokenServices.UserInfoTokenService;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenConsummationRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.response.BookTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
class UserInfoTokenServiceImpl implements UserInfoTokenService {


    @Value("${library.user.favourite_book.expiration_time_seconds}")
    private long expiration_time_seconds;

    private final UserInfoService userInfoService;
    private final FavouriteBookTokenRepository favouriteBookTokenRepository;
    private final UserInfoTokenServicesViewRepository userInfoTokenServicesViewRepository;

    public UserInfoTokenServiceImpl(UserInfoService userInfoService, FavouriteBookTokenRepository favouriteBookTokenRepository, UserInfoTokenServicesViewRepository userInfoTokenServicesViewRepository) {
        this.userInfoService = userInfoService;
        this.favouriteBookTokenRepository = favouriteBookTokenRepository;
        this.userInfoTokenServicesViewRepository = userInfoTokenServicesViewRepository;
    }

    @Transactional
    @Override
    public Either<GeneralError, BookTokenResponse> upsertFavouriteBookToken(BookTokenRequest bookTokenRequest) {
        Either<GeneralError, UUID> fetchedIdE = userInfoService.getUserInfoIdByEmail(bookTokenRequest.email());
        if (fetchedIdE.isLeft()) return Either.left(fetchedIdE.getLeft());
        BookTokenResponse token = userInfoTokenServicesViewRepository.fetchByUserId(fetchedIdE.get())
                .map(view -> {
                    if(view.getExpiresAt().isBefore(Instant.now())){
                        FavouriteBookToken newToken = createNewFavouriteBookToken(fetchedIdE.get());
                        return mapFavouriteBookTokenToBookTokenResponse(newToken, true);
                    }
                    else return mapMiniBookTokenViewToBookTokenResponse(view, false);
                })
                .orElseGet(() -> {
                    FavouriteBookToken newToken = createNewFavouriteBookToken(fetchedIdE.get());
                    return mapFavouriteBookTokenToBookTokenResponse(newToken, true);
                });
        return Either.right(token);
    }

    @Transactional
    FavouriteBookToken createNewFavouriteBookToken(UUID forUserId) {
        FavouriteBookToken newToken = FavouriteBookToken.builder()
                .token(UUID.randomUUID())
                .expiresAt(Instant.now().plusSeconds(expiration_time_seconds))
                .forUserId(forUserId)
                .useCount(0)
                .build();
        return favouriteBookTokenRepository.persist(newToken);
    }

    @Transactional
    @Override
    public Either<GeneralError, FavouriteBookMergerResponse> consumeFavouriteBookToken(BookTokenConsummationRequest consummationRequest) {
        return userInfoTokenServicesViewRepository.fetchByToken(consummationRequest.token())
                .map(token -> {
                    if(token.getExpiresAt().isBefore(Instant.now())) return Either.<GeneralError, FavouriteBookMergerResponse>left(new UserInfoError.FavouriteBookTokenExpired(consummationRequest.token()));
                    else{
                        favouriteBookTokenRepository.incrementTokenUsage(consummationRequest.token(), 1);
                        return userInfoService.mergeAndFetchFavouriteBooks(new FavouriteBookMergerRequest(token.getUserId(), consummationRequest.email()));
                    }
                })
                .orElseGet(() -> Either.left(new UserInfoError.FavouriteBookTokenNotFound(consummationRequest.token())));
    }

    private static BookTokenResponse mapFavouriteBookTokenToBookTokenResponse(FavouriteBookToken favouriteBookToken, boolean justCreated) {
        return new BookTokenResponse(favouriteBookToken.getToken().toString(), favouriteBookToken.getExpiresAt(), favouriteBookToken.getUseCount(), justCreated);
    }

    private static BookTokenResponse mapMiniBookTokenViewToBookTokenResponse(MiniFavouriteBookTokenView view, boolean justCreated) {
        return new BookTokenResponse(view.getToken().toString(), view.getExpiresAt(), view.getUseCount(), justCreated);
    }
}
