package org.library.thelibraryj.authentication.jwtAuth.domain;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.library.thelibraryj.authentication.userAuth.domain.UserRole;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private Servlet servlet;
    @InjectMocks
    private JwtFilter jwtFilter;

    private static final String token = "eyJhbGciOiJFUzI1NiJ9.eyJpYXQiOjE3MzE2MTQ0OTEsInN1YiI6InNhbXBsZS5lbWFpbDFAZ21haWwuY29tIiwiaXNzIjoiYTM4MWU0Mjc5Zjg2NDk2MzljMjE3ZTk1Yjk4ZmMyYTA1NTU3Y2MxNmFjZGQ2Y2NmMWMzMDVkZjI2OGQzY2I4MyIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MiIsImV4cCI6OTk5OTk5OTk5OX0.RGHc_rTxTzYIQpZL8iY85-iRWFsvSAyXg1oQt1RJH4ieinVTC6bvm812Pg6zvCFgjkDPEYgsVs_FjRdYb469pQ";
    private static final String refreshToken = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3MzE2MTQ0MjAsInN1YiI6InNhbXBsZS5lbWFpbDFAZ21haWwuY29tIiwiaXNzIjoiYTM4MWU0Mjc5Zjg2NDk2MzljMjE3ZTk1Yjk4ZmMyYTA1NTU3Y2MxNmFjZGQ2Y2NmMWMzMDVkZjI2OGQzY2I4MyIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MiIsImV4cCI6OTk5OTk5OTk5fQ.Ict_1rckKZ4p_q0WWtItH-z8qMck_EgxJuf07VzCMxVZDCNqP5Ay9NWD5XEStlZj9-mSR1seRhiEmRlonsqGWA";
    private static final String subject = "sample.email1@gmail.com";

    private HttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private User user;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain(servlet, jwtFilter);
        user = new User(subject, "pass", List.of(UserRole.USER));
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getRequestURI()).thenReturn("uriWithoutNa");
    }

    @AfterEach
    void resetContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testFilterInternalSuccess() throws Exception {
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());

        when(jwtService.validateToken(token)).thenReturn(user);

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertEquals(List.of(UserRole.USER), authentication.getAuthorities());
        Assertions.assertEquals(user, authentication.getPrincipal());
        Assertions.assertEquals(new WebAuthenticationDetailsSource().buildDetails(request), authentication.getDetails());
    }

    @Test
    public void testFilterInternalFail() {
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());

        when(jwtService.validateToken(token)).thenThrow(new UsernameNotFoundException(""));

        Assertions.assertThrows(UsernameNotFoundException.class, () -> jwtFilter.doFilterInternal(request, response, filterChain));

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
