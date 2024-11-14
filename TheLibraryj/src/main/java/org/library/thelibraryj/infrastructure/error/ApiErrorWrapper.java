package org.library.thelibraryj.infrastructure.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.library.thelibraryj.infrastructure.error.errorTypes.ActivationError;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.PasswordResetError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserAuthError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserInfoError;
import org.springframework.http.HttpStatus;

@Data
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
            case BookError.BookDetailEntityNotFound e -> getErrorResponse(error, HttpStatus.NOT_FOUND,
                    "Book data (details) missing. Id: " + e.missingEntityId());
            case BookError.BookPreviewEntityNotFound e -> getErrorResponse(error, HttpStatus.NOT_FOUND,
                    "Book data (preview) missing. Id: " + e.missingEntityId());
            case BookError.DuplicateTitle e -> getErrorResponse(error, HttpStatus.CONFLICT, "Duplicate title.");
            case BookError.UserNotAuthor e ->
                    getErrorResponse(error, HttpStatus.CONFLICT, "User is not the book author. Id: " + e.userId());
            case BookError.ChapterNotFound e ->
                    getErrorResponse(error, HttpStatus.NOT_FOUND, "Chapter not found. BookId: " + e.bookId() + "; Chapter number: " + e.chapterNumber());
            case UserInfoError.UserInfoEntityNotFound e -> getErrorResponse(error, HttpStatus.NOT_FOUND,
                    "User data (details) missing. Id: " + e.missingEntityId());
            case UserInfoError.UserAccountTooYoung e -> getErrorResponse(error, HttpStatus.BAD_REQUEST,
                    "User account too young to complete the desired action. Missing account age (hours):" + e.accountAgeMissing() + " Id: " + e.userId());
            case UserInfoError.UsernameNotUnique e ->
                    getErrorResponse(error, HttpStatus.CONFLICT, "Username not unique");
            case UserInfoError.UsernameUpdateCooldown e ->
                    getErrorResponse(error, HttpStatus.BAD_REQUEST, "Username update cooldown has not yet finished. Time left (hours): " + e.cooldownDurationLeft());
            case UserAuthError.UserAuthNotFoundId e ->
                    getErrorResponse(error, HttpStatus.NOT_FOUND, "User authentication data missing. Id: " + e.id());
            case UserAuthError.EmailNotUnique e ->
                    getErrorResponse(error, HttpStatus.CONFLICT, "Email not unique. Duplicate email: " + e.email());
            case UserAuthError.UserAuthNotFoundEmail e ->
                    getErrorResponse(error, HttpStatus.NOT_FOUND, "User authentication data missing. Email: " + e.email());
            case UserAuthError.UsernameNotUnique e ->
                    getErrorResponse(error, HttpStatus.CONFLICT, "Username not unique. Duplicate username: " + e.username());
            case UserAuthError.UserNotEnabled e ->
                    getErrorResponse(error, HttpStatus.BAD_REQUEST, "User not enabled. Email: " + e.email());
            case UserAuthError.GoogleApiNotResponding e ->
                getErrorResponse(error, HttpStatus.SERVICE_UNAVAILABLE, "Google api not responding to query.");
            case UserAuthError.UserIsGoogleRegistered e ->
                getErrorResponse(error, HttpStatus.BAD_REQUEST, "Google registered user can only login and change their password by Google api. Email: " + e.email());
            case ActivationError.UserAlreadyEnabled e ->
                    getErrorResponse(error, HttpStatus.BAD_REQUEST, "User already enabled. Id: " + e.userId());
            case ActivationError.ActivationTokenNotFound e ->
                    getErrorResponse(error, HttpStatus.NOT_FOUND, "Activation token missing. Token id: " + e.tokenId());
            case ActivationError.ActivationTokenExpired e ->
                    getErrorResponse(error, HttpStatus.BAD_REQUEST, "Activation token had already expired. Token for user with id: " + e.userId());
            case ActivationError.ActivationTokenAlreadyUsed e ->
                    getErrorResponse(error, HttpStatus.BAD_REQUEST, "Activation token has already been used. Token for user with id: " + e.userId());
            case PasswordResetError.PasswordResetTokenNotFound e ->
                    getErrorResponse(error, HttpStatus.NOT_FOUND, "Password reset token missing. Token id: " + e.tokenId());
            case PasswordResetError.PasswordResetTokenExpired e ->
                    getErrorResponse(error, HttpStatus.BAD_REQUEST, "Password reset token had already expired. Token for user with id: " + e.userId());
            case PasswordResetError.PasswordResetTokenAlreadyUsed e ->
                    getErrorResponse(error, HttpStatus.BAD_REQUEST, "Password reset token has already been used. Token for user with id: " + e.userId());
            case ServiceError.DatabaseError e -> getErrorResponse(error, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong on persistence layer. Consider reuploading corrupted/missing files.");
        };
    }

    private static ApiErrorResponse getErrorResponse(GeneralError error, HttpStatus status, String message) {
        return ApiErrorResponse.builder()
                .code(status.value())
                .status(status.getReasonPhrase())
                .path(error.getClass().getSimpleName())
                .message(message)
                .build();
    }
}
