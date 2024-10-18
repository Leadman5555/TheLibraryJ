package org.library.thelibraryj.userDetails.domain;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.GeneralError;
import org.library.thelibraryj.userDetails.dto.UserDetailsResponse;

import java.util.UUID;

record UserDetailsService() implements org.library.thelibraryj.userDetails.UserDetailsService {

    @Override
    public boolean existsById(UUID userId) {
        return true;
    }

    @Override
    public Either<GeneralError, UserDetailsResponse> getById(UUID userId) {
        return Either.right(new UserDetailsResponse(UUID.randomUUID(), "sampleName"));
    }

    @Override
    public Either<GeneralError, String> getUsernameById(UUID userId) {
        return Either.right("sampleName");
    }
}
