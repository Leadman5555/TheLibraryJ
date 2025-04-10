package org.library.thelibraryj.authentication.jwtAuth.domain;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JwtFilter extends OncePerRequestFilter {

    private final RequestMatcher whileListMatcher;
    private final JwtService jwtService;

    public JwtFilter(@Nullable String[] authWhiteList, JwtService jwtService) {
        if (authWhiteList == null) this.whileListMatcher = any -> false;
        else this.whileListMatcher = new OrRequestMatcher(
                Arrays.stream(authWhiteList)
                        .map(AntPathRequestMatcher::new)
                        .collect(Collectors.toUnmodifiableList())
            );
        this.jwtService = jwtService;
    }

    /**
     * Filters incoming requests to authenticate and validate JWT tokens.
     * Does not handle token refresh logic.
     * If authentication is successful, the security context is updated.
     * Bearer token in the header is validated.
     * Valid token -> pass
     * Invalid token -> 401 failure
     * Expired token -> 403 failure
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (whileListMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new JWTVerificationException("Invalid authorization header.");
        UserDetails validatedDetails = jwtService.validateToken(authHeader.substring(7));
        if (validatedDetails == null) throw new JWTVerificationException("Invalid JWT claims.");
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(validatedDetails, null, validatedDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
