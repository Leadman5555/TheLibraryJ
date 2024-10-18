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
    @JsonProperty("Error")
    private ApiErrorResponse errorResponse;

    public ApiErrorWrapper(GeneralError error) {
        this.errorResponse = generateWrapper(error);
    }

    private static ApiErrorResponse generateWrapper(GeneralError error){
        return switch (error){
            case BookError.BookDetailEntityNotFound e -> getErrorResponse(error, HttpStatus.BAD_REQUEST,
                    "Book data (details) missing.");
            case BookError.BookPreviewEntityNotFound e -> getErrorResponse(error, HttpStatus.BAD_REQUEST,
                    "Book data (preview) missing.");
            case UserDetailsError.UserEntityNotFound e ->  getErrorResponse(error, HttpStatus.BAD_REQUEST,
                    "User data (details) missing.");
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
