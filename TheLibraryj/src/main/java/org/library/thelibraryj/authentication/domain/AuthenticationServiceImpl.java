package org.library.thelibraryj.authentication.domain;

import io.vavr.control.Either;
import jakarta.mail.MessagingException;
import org.library.thelibraryj.authentication.AuthenticationService;
import org.library.thelibraryj.authentication.dto.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.AuthenticationResponse;
import org.library.thelibraryj.authentication.dto.RegisterRequest;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationRequest;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationResponse;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.AccountActivationTemplate;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.springframework.security.crypto.password.PasswordEncoder;

public record AuthenticationServiceImpl(UserAuthService userAuthService, EmailService emailService, AuthenticationProperties properties, PasswordEncoder passwordEncoder) implements AuthenticationService {
    @Override
    public Either<GeneralError, AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        return null;
    }

    @Override
    public Either<GeneralError, Boolean> register(RegisterRequest registerRequest) throws MessagingException {
        Either<GeneralError, UserCreationResponse> createdUser = createUser(registerRequest);
        if(createdUser.isLeft()) return Either.left(createdUser.getLeft());
        emailService.sendEmail(new EmailRequest(
                registerRequest.email(), new AccountActivationTemplate(registerRequest.username(), properties.getActivation_link())
        ));
        return null;
    }

    private Either<GeneralError, UserCreationResponse> createUser(RegisterRequest registerRequest) {
        UserCreationRequest creationRequest = new UserCreationRequest(
                registerRequest.email(),
                passwordEncoder.encode(new String(registerRequest.password())).toCharArray(),
                registerRequest.username()
        );
        registerRequest.zeroPassword(registerRequest.password());
        return userAuthService.createNewUser(creationRequest);
    }
}
