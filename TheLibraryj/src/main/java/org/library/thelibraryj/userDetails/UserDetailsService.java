package org.library.thelibraryj.userDetails;

import io.vavr.control.Either;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.userDetails.dto.UserDetailsResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserDetailsService {
    boolean existsById(UUID userId);
    Either<GeneralError, UserDetailsResponse> getById(UUID userId);
    Either<GeneralError, String> getUsernameById(UUID userId);
}
