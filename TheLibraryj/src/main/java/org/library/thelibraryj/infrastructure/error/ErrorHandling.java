package org.library.thelibraryj.infrastructure.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.exception.JsonDeserializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * The ErrorHandling interface provides utility methods to handle responses within a controller
 * by generating consistent success and error responses in a structured format.
 * Maps service return values to HTTP responses, handling errors by using
 * the statuses and messages defined in ApiErrorWrapper.
 */
public interface ErrorHandling {
    ObjectWriter ow = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter();
    private static ResponseEntity<String> createSuccessResponse(Object responseBody, HttpStatus successReturn) {
        return ResponseEntity.status(successReturn).body(toJson(responseBody));
    }

    private static ResponseEntity<String> createErrorResponse(ApiErrorWrapper errorWrapper) {
        return ResponseEntity.status(errorWrapper.getErrorResponse().code()).body(toJson(errorWrapper));
    }

    private static String toJson(Object object) {
        return Try.of(() -> ow.writeValueAsString(object))
                .getOrElseThrow(() ->new JsonDeserializationException(object.getClass().getSimpleName()));
    }

    default ResponseEntity<String> handle(Either<GeneralError, ?> serviceReturn, HttpStatus successReturn){
        return serviceReturn.mapLeft(ApiErrorWrapper::new)
                .fold(ErrorHandling::createErrorResponse, e -> createSuccessResponse(e, successReturn));
    }

    default ResponseEntity<String> handleError(Either<GeneralError, ?> errorServiceReturn){
        return createErrorResponse(new ApiErrorWrapper(errorServiceReturn.getLeft()));
    }

    default ResponseEntity<String> handleSuccess(Object serviceReturnBody, HttpStatus successReturn){
        return createSuccessResponse(serviceReturnBody, successReturn);
    }
}
