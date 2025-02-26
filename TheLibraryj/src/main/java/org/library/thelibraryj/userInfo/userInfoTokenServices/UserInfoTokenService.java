package org.library.thelibraryj.userInfo.userInfoTokenServices;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.tokenServices.TokenService;
import org.library.thelibraryj.userInfo.dto.response.FavouriteBookMergerResponse;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenConsummationRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.request.BookTokenRequest;
import org.library.thelibraryj.userInfo.userInfoTokenServices.dto.response.BookTokenResponse;

public interface UserInfoTokenService extends TokenService {
    Either<GeneralError, BookTokenResponse> upsertFavouriteBookToken(BookTokenRequest bookTokenRequest);
    Either<GeneralError, FavouriteBookMergerResponse> consumeFavouriteBookToken(BookTokenConsummationRequest consummationRequest);
    Either<GeneralError, Boolean> sendTokenToEmail(BookTokenConsummationRequest consummationRequest);
}
