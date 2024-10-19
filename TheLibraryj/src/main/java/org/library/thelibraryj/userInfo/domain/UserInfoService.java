package org.library.thelibraryj.userInfo.domain;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
record UserInfoService() implements org.library.thelibraryj.userInfo.UserInfoService {

    @Override
    public boolean existsById(UUID userId) {
        return true;
    }

    @Override
    public Either<GeneralError, UserInfoResponse> getById(UUID userId) {
        return Either.right(new UserInfoResponse(UUID.randomUUID(), "sampleName"));
    }

    @Override
    public Either<GeneralError, String> getAuthorUsernameAndCheckValid(UUID userId) {
        //Either.left(new UserDetailsError.UserAccountTooYoung(userId));
        return Either.right("sampleName");
    }
}
