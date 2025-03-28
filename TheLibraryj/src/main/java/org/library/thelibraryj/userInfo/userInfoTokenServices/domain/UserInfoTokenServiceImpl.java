package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import io.vavr.control.Either;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.EmailTemplate;
import org.library.thelibraryj.email.template.FavouriteBookTokenTemplate;
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

    private final long expiration_time_seconds;

    private final UserInfoService userInfoService;
    private final FavouriteBookTokenRepository favouriteBookTokenRepository;
    private final UserInfoTokenServicesViewRepository userInfoTokenServicesViewRepository;
    private final EmailService emailService;

    public UserInfoTokenServiceImpl(@Value("${library.user.favourite_book.expiration_time_seconds}") long expiration_time_seconds, UserInfoService userInfoService, FavouriteBookTokenRepository favouriteBookTokenRepository, UserInfoTokenServicesViewRepository userInfoTokenServicesViewRepository, EmailService emailService) {
        this.userInfoService = userInfoService;
        this.favouriteBookTokenRepository = favouriteBookTokenRepository;
        this.userInfoTokenServicesViewRepository = userInfoTokenServicesViewRepository;
        this.emailService = emailService;
        this.expiration_time_seconds = expiration_time_seconds;
    }

    @Transactional
    @Override
    public Either<GeneralError, BookTokenResponse> upsertFavouriteBookToken(BookTokenRequest bookTokenRequest) {
        Either<GeneralError, UUID> fetchedIdE = userInfoService.getUserInfoIdByEmail(bookTokenRequest.email());
        if (fetchedIdE.isLeft()) return Either.left(fetchedIdE.getLeft());
        BookTokenResponse token = userInfoTokenServicesViewRepository.fetchByUserId(fetchedIdE.get())
                .map(view -> {
                    if(isTokenExpired(view.getExpiresAt())){
                        FavouriteBookToken newToken = createNewFavouriteBookToken(fetchedIdE.get());
                        return mapFavouriteBookTokenToBookTokenResponse(newToken);
                    }
                    else return mapMiniBookTokenViewToBookTokenResponse(view);
                })
                .orElseGet(() -> {
                    FavouriteBookToken newToken = createNewFavouriteBookToken(fetchedIdE.get());
                    return mapFavouriteBookTokenToBookTokenResponse(newToken);
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
                    if(isTokenExpired(token.getExpiresAt())) return Either.<GeneralError, FavouriteBookMergerResponse>left(new UserInfoError.FavouriteBookTokenExpired(consummationRequest.token()));
                    else{
                        favouriteBookTokenRepository.incrementTokenUsage(consummationRequest.token(), 1);
                        return userInfoService.mergeFavouriteBooks(new FavouriteBookMergerRequest(token.getUserId(), consummationRequest.email()));
                    }
                })
                .orElseGet(() -> Either.left(new UserInfoError.FavouriteBookTokenNotFound(consummationRequest.token())));
    }

    @Override
    public Either<GeneralError, Boolean> sendTokenToEmail(BookTokenConsummationRequest consummationRequest) {
        Either<GeneralError, UUID> fetchedIdE = userInfoService.getUserInfoIdByEmail(consummationRequest.email());
        if (fetchedIdE.isLeft()) return Either.left(fetchedIdE.getLeft());
        return userInfoTokenServicesViewRepository.fetchByUserIdAndToken(fetchedIdE.get(), consummationRequest.token())
                .map(token -> {
                    if(isTokenExpired(token.getExpiresAt())) return Either.<GeneralError, Boolean>left(new UserInfoError.FavouriteBookTokenExpired(consummationRequest.token()));
                    else{
                        EmailTemplate emailTemplate = new FavouriteBookTokenTemplate(consummationRequest.token(), token.getUseCount(), token.getExpiresAt());
                        emailService.sendEmail(new EmailRequest(consummationRequest.email(), emailTemplate));
                        return Either.<GeneralError, Boolean>right(true);
                    }
                })
                .orElseGet(() -> Either.left(new UserInfoError.FavouriteBookTokenNotFound(consummationRequest.token())));
    }

    private static BookTokenResponse mapFavouriteBookTokenToBookTokenResponse(FavouriteBookToken favouriteBookToken) {
        return new BookTokenResponse(favouriteBookToken.getToken().toString(), favouriteBookToken.getExpiresAt(), favouriteBookToken.getUseCount(), true);
    }

    private static BookTokenResponse mapMiniBookTokenViewToBookTokenResponse(MiniFavouriteBookTokenView view) {
        return new BookTokenResponse(view.getToken().toString(), view.getExpiresAt(), view.getUseCount(), false);
    }

    private static boolean isTokenExpired(Instant expiresAt){
        return expiresAt.isBefore(Instant.now());
    }

    @Transactional
    @Override
    public void clearInvalidTokens() {
        favouriteBookTokenRepository.deleteAllExpired();
    }
}
