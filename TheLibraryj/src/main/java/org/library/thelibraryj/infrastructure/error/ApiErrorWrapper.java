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

@Data
@Slf4j
public class ApiErrorWrapper {
    @JsonProperty("error")
    private ApiErrorResponse errorResponse;

    public ApiErrorWrapper(GeneralError error) {
        this.errorResponse = generateWrapper(error);
    }

    public ApiErrorWrapper(ApiErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    @SuppressWarnings("unused")
    private static ApiErrorResponse generateWrapper(GeneralError error) {
        return switch (error) {
            case BookError.BookDetailEntityNotFound e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "Book data (details) missing. Id: " + e.missingEntityId());
            case BookError.BookPreviewEntityNotFound e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "Book data (preview) missing. Id: " + e.missingEntityId());
            case BookError.DuplicateTitle e -> getErrorResponseAndLog(error, HttpStatus.CONFLICT, "Duplicate title.");
            case BookError.UserNotAuthor e ->
                    getErrorResponseAndLog(error, HttpStatus.CONFLICT, "User is not the book author. Email: " + e.userEmail());
            case BookError.ChapterNotFound e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "Chapter not found. BookId: " + e.bookId() + "; Chapter number: " + e.chapterNumber());
            case UserInfoError.UserInfoEntityNotFound e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "User data (details) missing. Email: " + e.missingEntityEmail());
            case UserInfoError.UserInfoEntityNotFoundById e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "User data (details) missing by Id");
            case UserInfoError.UserInfoEntityNotFoundUsername e -> getErrorResponseAndLog(error, HttpStatus.NOT_FOUND,
                    "User data (details) missing. Username: " + e.missingEntityUsername());
            case UserInfoError.UserAccountTooYoung e -> getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST,
                    "User account too young to complete the desired action. Missing account age (hours):" + e.accountAgeMissing() + " Email: " + e.userEmail());
            case UserInfoError.UsernameNotUnique e ->
                    getErrorResponseAndLog(error, HttpStatus.CONFLICT, "Username not unique");
            case UserInfoError.UsernameUpdateCooldown e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Username update cooldown has not yet finished. Time left (hours): " + e.cooldownDurationLeft());
            case UserInfoError.ProfileImageUpdateFailed e ->
                    getErrorResponseAndLog(error, HttpStatus.INTERNAL_SERVER_ERROR, "Profile picture update failed.");
            case UserInfoError.UserNotEligibleForRankIncrease e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "User not eligible for rank increase. Missing score: " + e.missingScore() + " Email: " + e.email());
            case UserAuthError.UserAuthNotFoundId e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "User authentication data missing.");
            case UserAuthError.EmailNotUnique e ->
                    getErrorResponseAndLog(error, HttpStatus.CONFLICT, "Email not unique. Duplicate email: " + e.email());
            case UserAuthError.UserAuthNotFoundEmail e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "User authentication data missing. Email: " + e.email());
            case UserAuthError.UsernameNotUnique e ->
                    getErrorResponseAndLog(error, HttpStatus.CONFLICT, "Username not unique. Duplicate username: " + e.username());
            case UserAuthError.UserNotEnabled e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "User not enabled. Email: " + e.email());
            case UserAuthError.GoogleApiNotResponding e ->
                getErrorResponseAndLog(error, HttpStatus.SERVICE_UNAVAILABLE, "Google api not responding to query.");
            case UserAuthError.UserIsGoogleRegistered e ->
                getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Google registered user can only login and change their password by Google api. Email: " + e.email());
            case ActivationError.UserAlreadyEnabled e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "User already enabled. Email: " + e.email());
            case ActivationError.ActivationTokenNotFound e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "Activation token missing. Token id: " + e.tokenId());
            case ActivationError.ActivationTokenExpired e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Activation token had already expired. Token id: " + e.tokenId());
            case ActivationError.ActivationTokenAlreadyUsed e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Activation token has already been used. Token id: " + e.tokenId());
            case PasswordResetError.PasswordResetTokenNotFound e ->
                    getErrorResponseAndLog(error, HttpStatus.NOT_FOUND, "Password reset token missing. Token id: " + e.tokenId());
            case PasswordResetError.PasswordResetTokenExpired e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Password reset token had already expired. Token id: " + e.tokenId());
            case PasswordResetError.PasswordResetTokenAlreadyUsed e ->
                    getErrorResponseAndLog(error, HttpStatus.BAD_REQUEST, "Password reset token has already been used. Token id: " + e.tokenId());
            case ServiceError.DatabaseError e -> getErrorResponseAndLog(error, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong on persistence layer. Consider reuploading corrupted/missing files.");
        };
    }

    private static ApiErrorResponse getErrorResponseAndLog(GeneralError error, HttpStatus status, String message) {
        log.info(message);
        return ApiErrorResponse.builder()
                .code(status.value())
                .status(status.getReasonPhrase())
                .path(error.getClass().getSimpleName())
                .message(message)
                .build();
    }
}
