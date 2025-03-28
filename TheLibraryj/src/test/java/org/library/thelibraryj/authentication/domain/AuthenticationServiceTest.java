package org.library.thelibraryj.authentication.domain;

import io.vavr.control.Either;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.authentication.dto.request.AuthenticationRequest;
import org.library.thelibraryj.authentication.dto.response.AuthenticationResponse;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.library.thelibraryj.authentication.jwtAuth.dto.AuthToken;
import org.library.thelibraryj.authentication.userAuth.UserAuthService;
import org.library.thelibraryj.authentication.userAuth.domain.LoginDataView;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserAuthError;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testAuthenticateSuccess() {
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password".toCharArray());
        LoginDataView loginDataView = mock(LoginDataView.class);

        when(userAuthService.getLoginDataByEmail(request.email()))
                .thenReturn(Either.right(loginDataView));
        when(loginDataView.getIsGoogleUser()).thenReturn(false);
        when(loginDataView.getIsEnabled()).thenReturn(true);
        when(loginDataView.getGrantedAuthorities()).thenReturn(null);
        when(jwtService.generateToken(request.email())).thenReturn(mock(AuthToken.class));
        when(jwtService.generateRefreshToken(request.email())).thenReturn(mock(Cookie.class));

        Either<GeneralError, AuthenticationResponse> result = authenticationServiceImpl.authenticate(request);

        assertTrue(result.isRight());
        AuthenticationResponse response = result.get();
        assertNotNull(response.token());
        assertNotNull(response.refreshToken());
    }

    @Test
    void testAuthenticateFailUserIsGoogleRegistered() {
        AuthenticationRequest request = new AuthenticationRequest("googleuser@example.com", "password".toCharArray());
        LoginDataView loginDataView = mock(LoginDataView.class);

        when(userAuthService.getLoginDataByEmail(request.email()))
                .thenReturn(Either.right(loginDataView));
        when(loginDataView.getIsGoogleUser()).thenReturn(true);

        Either<GeneralError, AuthenticationResponse> result = authenticationServiceImpl.authenticate(request);

        assertTrue(result.isLeft());
        assertInstanceOf(UserAuthError.UserIsGoogleRegistered.class, result.getLeft());
    }

    @Test
    void testAuthenticateFailUserNotEnabled() {
        AuthenticationRequest request = new AuthenticationRequest("notenabled@example.com", "password".toCharArray());
        LoginDataView loginDataView = mock(LoginDataView.class);

        when(userAuthService.getLoginDataByEmail(request.email()))
                .thenReturn(Either.right(loginDataView));
        when(loginDataView.getIsGoogleUser()).thenReturn(false);
        when(loginDataView.getIsEnabled()).thenReturn(false);

        Either<GeneralError, AuthenticationResponse> result = authenticationServiceImpl.authenticate(request);

        assertTrue(result.isLeft());
        assertInstanceOf(UserAuthError.UserNotEnabled.class, result.getLeft());
    }
}