package org.library.thelibraryj.authentication.userAuth.domain;

import io.vavr.control.Either;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.authentication.userAuth.dto.UserCreationData;
import org.library.thelibraryj.authentication.userAuth.dto.request.UserCreationRequest;
import org.library.thelibraryj.infrastructure.error.errorTypes.GeneralError;
import org.library.thelibraryj.infrastructure.error.errorTypes.UserAuthError;
import org.library.thelibraryj.userInfo.UserInfoService;
import org.library.thelibraryj.userInfo.dto.request.UserInfoRequest;
import org.library.thelibraryj.userInfo.dto.response.UserInfoWithImageResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserAuthServiceTest {
    @Mock
    private UserAuthRepository userAuthRepository;
    @Mock
    private UserInfoService userInfoService;
    @Spy
    private final UserAuthMapper mapper = new UserAuthMapperImpl();
    @InjectMocks
    private UserAuthServiceImpl userAuthService;

    private String email;
    private char[] password;
    private String username;
    private UUID id;
    UserAuth userAuth;

    @BeforeEach
    public void setUp() {
        email = "sample@email.com";
        password = "password".toCharArray();
        username = "sample";
        id = UUID.randomUUID();
        userAuth = new UserAuth(
                id,
                0L,
                Instant.now(),
                Instant.now(),
                password,
                email,
                UserRole.ROLE_USER,
                false,
                false
        );
    }

    @Test
    public void testCreateNewUser() {
        UserCreationRequest request = new UserCreationRequest(email, password, username, null);
        UserInfoRequest infoRequest = new UserInfoRequest(username, email, id);
        UserAuth newAuth = mapper.userAuthRequestToUserAuth(request);
        newAuth.setRole(UserRole.ROLE_USER);
        when(userAuthRepository.persist(newAuth)).thenReturn(userAuth);
        when(userAuthRepository.existsByEmail(email)).thenReturn(false);
        when(userInfoService.existsByUsername(username)).thenReturn(false);
        when(userInfoService.createUserInfoWithImage(infoRequest, null)).thenReturn(new UserInfoWithImageResponse(
                username,
                email,
                0,
                0,
                null,
                (short) 0,
                null
        ));
        Either<GeneralError, UserCreationData> response = userAuthService.createNewUser(request);
        Assertions.assertTrue(response.isRight());
        Assertions.assertEquals(UserRole.ROLE_USER, response.get().role());
        Assertions.assertEquals(email, response.get().email());
        Assertions.assertFalse(response.get().isEnabled());

        when(userInfoService.existsByUsername(username)).thenReturn(true);
        Either<GeneralError, UserCreationData> response2 = userAuthService.createNewUser(request);
        Assertions.assertTrue(response2.isLeft());
        Assertions.assertEquals(UserAuthError.UsernameNotUnique.class, response2.getLeft().getClass());

        when(userAuthRepository.existsByEmail(email)).thenReturn(true);
        Either<GeneralError, UserCreationData> response3 = userAuthService.createNewUser(request);
        Assertions.assertTrue(response3.isLeft());
        Assertions.assertEquals(UserAuthError.EmailNotUnique.class, response3.getLeft().getClass());
    }

    @Test
    public void testEnableUser() {
        when(userAuthRepository.findById(id)).thenReturn(Optional.ofNullable(userAuth));
        Assertions.assertFalse(userAuth.isEnabled());
        Either<GeneralError, Boolean> response = userAuthService.enableUser(id);
        verify(userAuthRepository).update(userAuth);
        Assertions.assertTrue(response.isRight());
        Assertions.assertTrue(response.get());
        Assertions.assertTrue(userAuth.isEnabled());
    }

    @Test
    public void testDisableUser() {
        when(userAuthRepository.findById(id)).thenReturn(Optional.ofNullable(userAuth));
        userAuth.setEnabled(true);
        Either<GeneralError, Boolean> response = userAuthService.disableUser(id);
        verify(userAuthRepository).update(userAuth);
        Assertions.assertTrue(response.isRight());
        Assertions.assertTrue(response.get());
        Assertions.assertFalse(userAuth.isEnabled());
    }

    @Test
    public void testUpdateUserPassword() {
        when(userAuthRepository.findById(id)).thenReturn(Optional.ofNullable(userAuth));
        Assertions.assertEquals(new String(password), userAuth.getPassword());
        char[] newPassword = ("newPassword").toCharArray();
        Either<GeneralError, Boolean> response = userAuthService.updatePassword(id, newPassword);
        verify(userAuthRepository).update(userAuth);
        Assertions.assertTrue(response.isRight());
        Assertions.assertTrue(response.get());
        Assertions.assertEquals(new String(newPassword), userAuth.getPassword());
    }

}
