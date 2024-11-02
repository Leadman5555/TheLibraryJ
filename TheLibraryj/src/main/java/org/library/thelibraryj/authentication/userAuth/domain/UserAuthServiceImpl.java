package org.library.thelibraryj.authentication.userAuth.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.dto.UserAuthRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserAuthResponse;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationResponse;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserAuthError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
class UserAuthServiceImpl implements UserAuthService {
    private final UserAuthRepository userAuthRepository;
    private final UserInfoService userInfoService;
    private final UserAuthMapper mapper;

    public UserAuthServiceImpl(UserAuthRepository userAuthRepository, UserAuthMapper mapper, UserInfoService userInfoService) {
        this.userAuthRepository = userAuthRepository;
        this.mapper = mapper;
        this.userInfoService = userInfoService;
    }

    @Transactional
    @Override
    public Either<GeneralError, UserCreationResponse> createNewUser(UserAuthRequest userAuthRequest) {
        if(existsByEmail(userAuthRequest.email())) return Either.left(new UserAuthError.EmailNotUnique(userAuthRequest.email()));
        if(userInfoService.existsByUsername(userAuthRequest.username())) return Either.left(new UserAuthError.UsernameNotUnique(userAuthRequest.username()));
        UserAuth newUserAuth = mapper.userAuthRequestToUserAuth(userAuthRequest);
        newUserAuth.setRole(UserRole.USER);
        UserAuth createdAuth = userAuthRepository.persist(newUserAuth);
        UserInfoResponse createdInfo = userInfoService.createUserInfo(new UserInfoRequest(
                userAuthRequest.username(),
                userAuthRequest.email(),
                createdAuth.getId()
        )).get();
        userAuthRepository.flush();
        return Either.right(mapper.userAuthAndUserInfoResponseToUserCreationResponse(createdAuth, createdInfo));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userAuthRepository.existsByEmail(email);
    }

    @Override
    public Either<GeneralError, Boolean> isEnabled(UUID userId) {
        return Try.of(() -> userAuthRepository.isEnabled(userId))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new UserAuthError.UserAuthNotFoundId(userId)));
    }

    @Transactional
    @Override
    public Either<GeneralError, Boolean> enableUser(UUID userId) {
        Either<GeneralError, UserAuth> fetched = findById(userId);
        if(fetched.isRight()) return Either.left(fetched.getLeft());
        UserAuth toUpdate = fetched.get();
        toUpdate.setEnabled(true);
        userAuthRepository.update(toUpdate);
        return Either.right(true);
    }

    @Transactional
    @Override
    public Either<GeneralError, Boolean> updatePassword(UUID userId, String newEncryptedPassword) {
        Either<GeneralError, UserAuth> fetched = findById(userId);
        if(fetched.isRight()) return Either.left(fetched.getLeft());
        UserAuth toUpdate = fetched.get();
        toUpdate.setPassword(newEncryptedPassword.toCharArray());
        userAuthRepository.update(toUpdate);
        return Either.right(true);
    }

    @Override
    public Either<GeneralError, UserAuthResponse> getResponseByEmail(String email) {
        Either<GeneralError, UserAuth> fetched = findByEmail(email);
        if (fetched.isRight()) return Either.right(mapper.userAuthToUserAuthResponse(
                fetched.get()
        ));
        return Either.left(fetched.getLeft());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAuthRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    Either<GeneralError, UserAuth> findByEmail(String email) {
        return Try.of(() -> userAuthRepository.findByEmail(email))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new UserAuthError.UserAuthNotFoundEmail(email)));
    }

    Either<GeneralError, UserAuth> findById(UUID id) {
        return Try.of(() -> userAuthRepository.findById(id))
                .toEither()
                .map(Option::ofOptional)
                .<GeneralError>mapLeft(ServiceError.DatabaseError::new)
                .flatMap(optionalEntity -> optionalEntity.toEither(new UserAuthError.UserAuthNotFoundId(id)));
    }
}
