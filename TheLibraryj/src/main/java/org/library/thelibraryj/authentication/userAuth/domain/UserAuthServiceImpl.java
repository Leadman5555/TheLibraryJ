package org.library.thelibraryj.authentication.userAuth.domain;

import io.vavr.control.Either;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.dto.UserAuthRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserAuthResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
class UserAuthServiceImpl implements UserAuthService {
    private final UserAuthRepository userAuthRepository;
    private final UserAuthMapper mapper;

    public UserAuthServiceImpl(UserAuthRepository userAuthRepository, UserAuthMapper mapper) {
        this.userAuthRepository = userAuthRepository;
        this.mapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    @Override
    public Either<GeneralError, UserAuthResponse> create(UserAuthRequest userAuthRequest) {
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public Either<GeneralError, Boolean> isEnabled(UUID userId) {
        return null;
    }

    @Override
    public Either<GeneralError, Boolean> enableUser(UUID userId) {
        return null;
    }

    @Override
    public Either<GeneralError, Boolean> updatePassword(UUID userId, String newEncryptedPassword) {
        return null;
    }

    @Override
    public Either<GeneralError, UserAuthResponse> findByEmail(String email) {
        return null;
    }
}
