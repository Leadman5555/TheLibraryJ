//package org.library.thelibraryj.authentication.jwtAuth.domain;
//
//import jakarta.servlet.Servlet;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.library.thelibraryj.authentication.jwtAuth.JwtService;
//import org.library.thelibraryj.authentication.userAuth.domain.UserRole;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockFilterChain;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class JwtFilterTest {
//    @Mock
//    private JwtService jwtService;
//    @Mock
//    private UserDetailsService userDetailsService;
//
//    @Mock
//    private Servlet servlet;
//    @InjectMocks
//    private JwtFilter jwtFilter;
//
//    private static final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYW1wbGUuZW1haWwxQGdtYWlsLmNvbSIsImlhdCI6MTczMTMyMTI2MiwiZXhwIjo5OTkxMzIxNjIyfQ.8lRW2KJjPOp4q3VDTYTRoiwOEOIi6wJ6i-WZXPmthnU";
//    private static final String subject = "sample.email1@gmail.com";
//
//    private HttpServletRequest request;
//    private MockHttpServletResponse response;
//    private MockFilterChain filterChain;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        request = mock(HttpServletRequest.class);
//        response = new MockHttpServletResponse();
//        filterChain = new MockFilterChain(servlet, jwtFilter);
//        user = new User(subject, "pass", List.of(UserRole.USER));
//        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//        when(jwtService.extractSubject(token)).thenReturn(subject);
//        when(userDetailsService.loadUserByUsername(subject)).thenReturn(user);
//    }
//
//    @AfterEach
//    void resetContext() {
//        SecurityContextHolder.clearContext();
//    }
//
//    @Test
//    public void testFilterInternalSuccess() throws Exception {
//        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
//
//        when(jwtService.validateToken(token, subject)).thenReturn(true);
//
//        jwtFilter.doFilterInternal(request, response, filterChain);
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Assertions.assertEquals(List.of(UserRole.USER), authentication.getAuthorities());
//        Assertions.assertEquals(user, authentication.getPrincipal());
//        Assertions.assertEquals(new WebAuthenticationDetailsSource().buildDetails(request), authentication.getDetails());
//    }
//
//    @Test
//    public void testFilterInternalFail() throws ServletException, IOException {
//        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
//
//        when(jwtService.validateToken(token, subject)).thenReturn(false);
//
//        jwtFilter.doFilterInternal(request, response, filterChain);
//
//        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//}
