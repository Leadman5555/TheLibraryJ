package org.library.thelibraryj.authentication.jwtAuth.domain;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.authentication.jwtAuth.JwtService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        } else {
            UserDetails validatedDetails;
            try {
                validatedDetails = jwtService.validateToken(authHeader.substring(7));
            }catch (TokenExpiredException expiredException){
                Cookie refreshToken = Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals("refresh-token"))
                        .findFirst().orElseThrow(() -> new AccessDeniedException("Session expired."));

                validatedDetails = jwtService.validateToken(refreshToken.getValue());
                if(validatedDetails == null) throw new AccessDeniedException("Invalid refresh token.");
                response.addHeader("Refreshed-jwt-token", jwtService.generateToken(validatedDetails.getUsername()).token());
            }
            if (validatedDetails != null) {
                if(SecurityContextHolder.getContext().getAuthentication() == null){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(validatedDetails, null, validatedDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                filterChain.doFilter(request, response);
            }else throw new AccessDeniedException("Invalid JWT claims.");
        }
    }
}
