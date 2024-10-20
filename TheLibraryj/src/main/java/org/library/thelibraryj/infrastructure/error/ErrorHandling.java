package org.library.thelibraryj.infrastructure.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.exception.JsonDeserializationException;
import org.springframework.http.ResponseEntity;

public interface ErrorHandling {
    ObjectWriter ow = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter();
    private static ResponseEntity<String> createSuccessResponse(Object responseBody) {
        return ResponseEntity.status(200).body(toJson(responseBody));
    }

    private static ResponseEntity<String> createErrorResponse(ApiErrorWrapper errorWrapper) {
        return ResponseEntity.status(errorWrapper.getErrorResponse().code()).body(toJson(errorWrapper));
    }

    private static String toJson(Object object) {
        return Try.of(() -> ow.writeValueAsString(object))
                .getOrElseThrow(e ->new JsonDeserializationException(object.getClass().getSimpleName()));
    }

    default ResponseEntity<String> handle(Either<GeneralError, ?> serviceReturn){
        return serviceReturn.mapLeft(ApiErrorWrapper::new)
                .fold(ErrorHandling::createErrorResponse, ErrorHandling::createSuccessResponse);
    }
}
