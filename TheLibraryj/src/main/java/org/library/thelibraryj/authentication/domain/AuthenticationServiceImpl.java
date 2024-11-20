package org.library.thelibraryj.authentication.domain;

import io.vavr.control.Either;
import jakarta.mail.MessagingException;
import org.library.thelibraryj.authentication.AuthenticationService;
import org.library.thelibraryj.authentication.PasswordControl;
import org.library.thelibraryj.authentication.dto.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.AuthenticationResponse;
import org.library.thelibraryj.authentication.dto.BasicUserDataRequest;
import org.library.thelibraryj.authentication.dto.RegisterRequest;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.library.thelibraryj.authentication.tokenServices.ActivationService;
import org.library.thelibraryj.authentication.tokenServices.dto.activation.ActivationTokenResponse;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.dto.LoginDataResponse;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationData;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationResponse;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.AccountActivationTemplate;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserAuthError;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
record AuthenticationServiceImpl(UserAuthService userAuthService,
                                 EmailService emailService,
                                 AuthenticationProperties properties,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 ActivationService activationService,
                                 JwtService jwtService) implements PasswordControl, AuthenticationService {
    @Override
    public Either<GeneralError, AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        Either<GeneralError, LoginDataResponse> fetchedE = userAuthService.getLoginDataByEmail(authenticationRequest.email());
        if(fetchedE.isLeft()) return Either.left(fetchedE.getLeft());
        LoginDataResponse fetched = fetchedE.get();
        if(fetched.isGoogleUser()) return Either.left(new UserAuthError.UserIsGoogleRegistered(authenticationRequest.email()));
        if (!fetched.isEnabled()) return Either.left(new UserAuthError.UserNotEnabled(authenticationRequest.email()));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.email(),
                        new String(authenticationRequest.password()),
                        fetched.grantedAuthorities())
        );
        zeroPassword(authenticationRequest.password());
        return Either.right(new AuthenticationResponse(
                jwtService().generateToken(authenticationRequest.email())
        ));
    }

    @Override
    public Either<GeneralError, UserCreationResponse> register(RegisterRequest registerRequest) throws MessagingException {
        Either<GeneralError, UserCreationData> createdUser = createUser(registerRequest);
        if (createdUser.isLeft()) return Either.left(createdUser.getLeft());
        UserCreationData data = createdUser.get();
        ActivationTokenResponse createdToken = activationService.createFirstActivationToken(data.userAuthId());
        sendActivationMail(registerRequest.username(), registerRequest.email(), createdToken);
        return Either.right(new UserCreationResponse(
                data.username(),
                data.rank(),
                data.currentScore(),
                data.dataUpdatedAt(),
                data.email(),
                data.isEnabled(),
                data.profileImage()
        ));
    }

    @Override
    public Either<GeneralError, Boolean> resendActivationEmail(BasicUserDataRequest basicUserDataRequest) throws MessagingException {
        Either<GeneralError, ActivationTokenResponse> createdTokenE = activationService.createActivationToken(basicUserDataRequest.email());
        if(createdTokenE.isLeft()) return Either.left(createdTokenE.getLeft());
        sendActivationMail(basicUserDataRequest.username(), basicUserDataRequest.email(), createdTokenE.get());
        return Either.right(true);
    }

    private void sendActivationMail(String forUsername, String forEmail, ActivationTokenResponse createdToken) throws MessagingException {
        emailService.sendEmail(new EmailRequest(
                forEmail,
                new AccountActivationTemplate(
                        forUsername,
                        properties.getActivation_link() + createdToken.token(),
                        createdToken.expiresAt()
                )));
    }

    private Either<GeneralError, UserCreationData> createUser(RegisterRequest registerRequest) {
        UserCreationRequest creationRequest = new UserCreationRequest(
                registerRequest.email(),
                passwordEncoder.encode(new String(registerRequest.password())).toCharArray(),
                registerRequest.username(),
                registerRequest.profileImage()
        );
        zeroPassword(registerRequest.password());
        return userAuthService.createNewUser(creationRequest);
    }
}
