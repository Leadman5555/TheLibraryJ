package org.library.thelibraryj.infrastructure.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.library.thelibraryj.infrastructure.error.errorTypes.ActivationError;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.PasswordResetError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserAuthError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.springframework.http.HttpStatus;

/**
 * A wrapper class for handling API error responses in a standardized format.
 * <p>
 * Supports predefined categories of errors defined in sealed interfaces, providing a match
 * for every possible error.
 * Maps given error to corresponding HTTP status code and message, as well as logging the error message in progress.
 */
@Data
@Slf4j
public class ApiErrorWrapper {
    @JsonProperty("errorDetails")
    private ApiErrorResponse errorResponse;

    public ApiErrorWrapper(GeneralError error) {
        this.errorResponse = generateWrapper(error);
    }

    public ApiErrorWrapper(ApiErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    private final static String NO_SERVER_DETAILS = "none";

    @SuppressWarnings("unused")
    private static ApiErrorResponse generateWrapper(GeneralError error) {
        return switch (error) {
            case BookError.BookDetailEntityNotFound e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "Book data (details) missing. Id: " + e.missingEntityId(), NO_SERVER_DETAILS);
            case BookError.BookPreviewEntityNotFound e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "Book data (preview) missing. Resource identifier: " + e.missingEntityIdentifier(), NO_SERVER_DETAILS);
            case BookError.DuplicateTitle e -> getErrorResponseAndLog(error, HttpStatus.CONFLICT, "Duplicate title - chosen title is already used.", e.bookIdentifier());
            case BookError.UserNotAuthor e ->
                    getErrorResponseAndLog(error, HttpStatus.CONFLICT, "User is not the book author. Email: " + e.userEmail(), e.bookId().toString());
            case BookError.ChapterNotFound e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "Chapter " + e.chapterNumber() + " not found", e.bookId().toString());
            case BookError.DuplicateChapter e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Request contains duplicate chapter numbers - duplicate number: " + e.chapterNumber(), e.bookId().toString());
            case BookError.InvalidChapterTitleFormat e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Invalid chapter title format - invalid title: " + e.title(), e.bookId().toString());
            case BookError.InvalidChapterTextLength e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Invalid chapter text length for chapter number: " + e.chapterNumber(), e.bookId().toString());
            case BookError.MalformedChapterText e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Malformed chapter text for chapter number: " + e.chapterNumber(), e.bookId().toString());
            case UserInfoError.UserInfoEntityNotFoundByEmail e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "User data (details) missing. Email: " + e.missingEntityEmail(), NO_SERVER_DETAILS);
            case UserInfoError.UserInfoEntityNotFoundById e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "User data (details) missing by Id", e.userId().toString());
            case UserInfoError.UserInfoEntityNotFoundByUsername e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "User data (details) missing. Username: " + e.missingEntityUsername(), NO_SERVER_DETAILS);
            case UserInfoError.UserAccountTooYoung e -> getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST,
                    "User account too young to complete the desired action. Missing account age (hours):" + e.accountAgeMissing(), e.userEmail());
            case UserInfoError.UsernameNotUnique e ->
                    getErrorResponseAndLog(error, HttpStatus.CONFLICT, "The chosen username is not unique.", e.userEmail());
            case UserInfoError.UsernameUpdateCooldown e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Username update cooldown has not yet finished. Time left (days): " + e.cooldownDurationLeft(), e.userEmail());
            case UserInfoError.ProfileImageUpdateFailed e ->
                    getErrorResponseAndLog(error, HttpStatus.INTERNAL_SERVER_ERROR, "Profile picture update failed. Please try again later.", e.userEmail());
            case UserInfoError.UserNotEligibleForRankIncrease e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "User not eligible for rank increase. Missing score: " + e.missingScore(), e.email());
            case UserInfoError.UserNotEligibleForChosenPreference e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "User not eligible for chosen preference. Missing rank: " + e.missingRank(), e.email());
            case UserInfoError.FavouriteBookTokenNotFound e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "Favourite book token not found. Please generate a new one.", e.tokenId().toString());
            case UserInfoError.FavouriteBookTokenExpired e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Favourite book token has expired. Please request a new one.", e.tokenId().toString());
            case UserAuthError.UserAuthNotFoundId e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "User authentication data missing.", e.userId().toString());
            case UserAuthError.EmailNotUnique e ->
                    getErrorResponseAndLog(error, HttpStatus.CONFLICT, "Chosen email address is not unique.", e.email());
            case UserAuthError.UserAuthNotFoundEmail e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "User authentication data missing. Email: " + e.email(), NO_SERVER_DETAILS);
            case UserAuthError.UsernameNotUnique e ->
                    getErrorResponseAndLog(error, HttpStatus.CONFLICT, "The chosen username is not unique.",  e.userEmail());
            case UserAuthError.UserNotEnabled e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "User not enabled. Please activate your account first.", e.email());
            case UserAuthError.UserIsGoogleRegistered e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Google registered user can only login and change their password using Google services.",  e.email());
            case ActivationError.UserAlreadyEnabled e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "User account is already enabled.", e.email());
            case ActivationError.ActivationTokenNotFound e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "Activation token not found. Please try again with a different one.", e.tokenId().toString());
            case ActivationError.ActivationTokenExpired e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Activation token has expired. Please request a new one.", e.tokenId().toString());
            case ActivationError.ActivationTokenAlreadyUsed e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Activation token has already been used. Please request a new one", e.tokenId().toString());
            case PasswordResetError.PasswordResetTokenNotFound e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "Password reset token not found. Please try again with a different one.", e.tokenId().toString());
            case PasswordResetError.PasswordResetTokenExpired e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Password reset token has expired. Please request a new one.", e.tokenId().toString());
            case PasswordResetError.PasswordResetTokenAlreadyUsed e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Password reset token has already been used. Please request a new one", e.tokenId().toString());
            case ServiceError.DatabaseError e -> getErrorResponseAndLog(error, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong on persistence layer.", NO_SERVER_DETAILS);
        };
    }

    private static ApiErrorResponse getErrorResponseAndLog(GeneralError error, HttpStatus status, String clientMessage, String serverResourceIdentifier) {
        log.info("{}; Server resource identifier: {}", clientMessage, serverResourceIdentifier);
        return ApiErrorResponse.builder()
                .code(status.value())
                .status(status.getReasonPhrase())
                .path(error.getClass().getSimpleName())
                .message(clientMessage)
                .build();
    }
}
