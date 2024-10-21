package org.library.thelibraryj.infrastructure.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.library.thelibraryj.infrastructure.error.errorTypes.BookError;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserDetailsError;
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

    private static ApiErrorResponse generateWrapper(GeneralError error){
        return switch (error){
            case BookError.BookDetailEntityNotFound e -> getErrorResponse(error, HttpStatus.BAD_REQUEST,
                    "Book data (details) missing. Id: " + e.missingEntityId());
            case BookError.BookPreviewEntityNotFound e -> getErrorResponse(error, HttpStatus.BAD_REQUEST,
                    "Book data (preview) missing. Id: " + e.missingEntityId());
            case BookError.DuplicateTitle e -> getErrorResponse(error, HttpStatus.CONFLICT, "Duplicate title.");
            case BookError.UserNotAuthor e -> getErrorResponse(error, HttpStatus.CONFLICT, "User is not the book author. Id: " + e.userId());
            case BookError.ChapterNotFound e -> getErrorResponse(error, HttpStatus.BAD_REQUEST, "Chapter not found. BookId: " + e.bookId() + "; Chapter number: " + e.chapterNumber());
            case UserDetailsError.UserDetailsEntityNotFound e ->  getErrorResponse(error, HttpStatus.BAD_REQUEST,
                    "User data (details) missing. Id: " + e.missingEntityId());
            case UserDetailsError.UserAccountTooYoung e ->getErrorResponse(error, HttpStatus.BAD_REQUEST,
                    "User account too young to complete the desired action. Id: " + e.userId());
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
