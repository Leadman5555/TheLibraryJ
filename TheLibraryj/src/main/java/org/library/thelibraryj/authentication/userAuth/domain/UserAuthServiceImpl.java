package org.library.thelibraryj.authentication.userAuth.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.dto.GoogleUserCreationRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationData;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationRequest;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.ServiceError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserAuthError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.UserInfoWithImageResponse;
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
    public Either<GeneralError, UserCreationData> createNewUser(UserCreationRequest userCreationRequest) {
        if (existsByEmail(userCreationRequest.email()))
            return Either.left(new UserAuthError.EmailNotUnique(userCreationRequest.email()));
        if (userInfoService.existsByUsername(userCreationRequest.username()))
            return Either.left(new UserAuthError.UsernameNotUnique(userCreationRequest.username()));
        UserAuth newUserAuth = mapper.userAuthRequestToUserAuth(userCreationRequest);
        newUserAuth.setRole(UserRole.USER);
        newUserAuth.setGoogle(false);
        UserAuth createdAuth = userAuthRepository.persist(newUserAuth);
        UserInfoWithImageResponse createdInfo = userInfoService.createUserInfoWithImage(new UserInfoRequest(
                userCreationRequest.username(),
                userCreationRequest.email(),
                createdAuth.getId()
        ), userCreationRequest.profileImage());
        userAuthRepository.flush();
        return Either.right(mapper.userAuthAndUserInfoResponseToUserCreationResponse(createdInfo, createdAuth));
    }

    @Transactional
    @Override
    public void createNewGoogleUser(GoogleUserCreationRequest userCreationRequest) {
        UserAuth newUser = UserAuth.builder()
                .role(UserRole.USER)
                .isGoogle(true)
                .email(userCreationRequest.email())
                .isEnabled(true)
                .build();
        UserAuth createdAuth = userAuthRepository.persist(newUser);
        userInfoService.createUserInfo(new UserInfoRequest(
                userCreationRequest.username(),
                userCreationRequest.email(),
                createdAuth.getId()
        ));
        userAuthRepository.flush();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userAuthRepository.existsByEmail(email);
    }

    @Override
    public Either<GeneralError, BasicUserAuthView> getBasicUserAuthDataByEmail(String email) {
       return Either.right(userAuthRepository.getBasicUserAuthData(email));
    }

    @Transactional
    @Override
    public Either<GeneralError, Boolean> enableUser(UUID userId) {
        Either<GeneralError, UserAuth> fetched = findById(userId);
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        UserAuth toUpdate = fetched.get();
        toUpdate.setEnabled(true);
        userAuthRepository.update(toUpdate);
        return Either.right(true);
    }

    @Transactional
    @Override
    public Either<GeneralError, Boolean> disableUser(UUID userId) {
        Either<GeneralError, UserAuth> fetched = findById(userId);
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        UserAuth toUpdate = fetched.get();
        toUpdate.setEnabled(false);
        userAuthRepository.update(toUpdate);
        return Either.right(true);
    }

    @Transactional
    @Override
    public Either<GeneralError, Boolean> updatePassword(UUID userId, char[] newEncryptedPassword) {
        Either<GeneralError, UserAuth> fetched = findById(userId);
        if (fetched.isLeft()) return Either.left(fetched.getLeft());
        UserAuth toUpdate = fetched.get();
        toUpdate.setPassword(newEncryptedPassword);
        userAuthRepository.update(toUpdate);
        return Either.right(true);
    }

    @Override
    public Either<GeneralError, PasswordResetView> getPasswordResetDataByEmail(String email) {
        return Either.right(userAuthRepository.getPasswordResetData(email));
    }

    @Override
    public Either<GeneralError, LoginDataView> getLoginDataByEmail(String email) {
        return Either.right(userAuthRepository.getLoginData(email));
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
                .flatMap(optionalEntity -> optionalEntity.toEither(new UserAuthError.UserAuthNotFoundId()));
    }
}
