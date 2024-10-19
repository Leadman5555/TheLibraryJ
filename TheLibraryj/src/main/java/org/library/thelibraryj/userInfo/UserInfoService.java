package org.library.thelibraryj.userInfo;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserInfoService {
    boolean existsById(UUID userId);
    Either<GeneralError, UserInfoResponse> getById(UUID userId);
    Either<GeneralError, String> getAuthorUsernameAndCheckValid(UUID userId);
}
