package org.library.thelibraryj.authentication.domain;

import io.vavr.control.Either;
import jakarta.mail.MessagingException;
import org.library.thelibraryj.authentication.AuthenticationService;
import org.library.thelibraryj.authentication.activation.ActivationService;
import org.library.thelibraryj.authentication.activation.dto.ActivationTokenResponse;
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
import org.library.thelibraryj.infrastructure.error.errorTypes.UserAuthError;
import org.library.thelibraryj.jwtAuth.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
record AuthenticationServiceImpl(UserAuthService userAuthService, EmailService emailService,
                                 AuthenticationProperties properties,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 ActivationService activationService,
                                 JwtService jwtService) implements org.library.thelibraryj.authentication.domain.PasswordControl, AuthenticationService {
    @Override
    public Either<GeneralError, AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        UserDetails fetched = userAuthService.loadUserByUsername(authenticationRequest.email());
        if (!fetched.isEnabled()) return Either.left(new UserAuthError.UserNotEnabled(authenticationRequest.email()));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.email(),
                        new String(authenticationRequest.password()),
                        fetched.getAuthorities())
        );
        zeroPassword(authenticationRequest.password());
        return Either.right(new AuthenticationResponse(
                jwtService().generateToken(fetched.getUsername())
        ));
    }

    @Override
    public Either<GeneralError, UserCreationResponse> register(RegisterRequest registerRequest) throws MessagingException {
        Either<GeneralError, UserCreationResponse> createdUser = createUser(registerRequest);
        if (createdUser.isLeft()) return Either.left(createdUser.getLeft());
        ActivationTokenResponse createdToken = activationService.createFirstActivationToken(createdUser.get().userId());
        emailService.sendEmail(new EmailRequest(
                registerRequest.email(),
                new AccountActivationTemplate(
                        registerRequest.username(),
                        properties.getActivation_link() + createdToken.token(),
                        createdToken.expiresAt()
                )));
        return createdUser;
    }

    private Either<GeneralError, UserCreationResponse> createUser(RegisterRequest registerRequest) {
        UserCreationRequest creationRequest = new UserCreationRequest(
                registerRequest.email(),
                passwordEncoder.encode(new String(registerRequest.password())).toCharArray(),
                registerRequest.username()
        );
        zeroPassword(registerRequest.password());
        return userAuthService.createNewUser(creationRequest);
    }
}
