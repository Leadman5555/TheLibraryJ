package org.library.thelibraryj.authentication.jwtAuth.domain;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * Filters incoming requests to authenticate and validate JWT tokens,
     * and handles token expiration and refresh logic.
     * If authentication is successful, the security context is updated.
     * Requests without JWT token in header are not authenticated.
     * Requests with the token in header are validated.
     * Valid token -> pass
     * Invalid token -> fail
     * Expired token -> refresh token from Http-only cookie is checked
     * Refresh token invalid/not present -> fail
     * Refresh token valid -> new JWT token attached as cookie to response, pass
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains("/na/")) filterChain.doFilter(request, response);
        else {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new JWTVerificationException("Invalid authorization header.");
            } else {
                UserDetails validatedDetails = jwtService.validateToken(authHeader.substring(7));
                if (validatedDetails != null) {
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(validatedDetails, null, validatedDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                    filterChain.doFilter(request, response);
                } else throw new JWTVerificationException("Invalid JWT claims.");
            }
        }
    }
}
