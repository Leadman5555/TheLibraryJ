package org.library.thelibraryj.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.authentication.jwtAuth.domain.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
class SecurityConfiguration {
    /** URLs allowed passing without any authentication **/
    private static final String[] AUTH_WHITELIST = {
            "/h2-console/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/v3/api-docs/**",
            "/v0.9/na/**"
    };
    private final UserDetailsService userDetailsService;
    private final FilterChainExceptionHandler filterChainExceptionHandler;

    @Value("${library.client.base_url}")
    private String clientBaseUrl;


//    /**
//     * Creates a CookieCsrfTokenRepository bean for managing CSRF tokens through browser cookies.
//     * Configured to automatically attach a XSRF-TOKEN cookie to any authenticated request.
//     * Checks each incoming authenticated request for:
//     * 1. XSRF-TOKEN as a cookie
//     * 2. X-XSRF-TOKEN header with value equal to that of the cookie
//     * 3. Valid value of the cookie
//     */
//    @Profile(value={"development"})
//    @Bean(name="csrfTokenRepository")
//    CookieCsrfTokenRepository cookieCsrfTokenRepository() {
//        final CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
//        repository.setCookiePath("/");
//        return repository;
//    }


    /**
     * URLs matching the AUTH_WHITELIST pass the filter chain without any checks.
     * Any other URL needs to be validated for the required credentials
     * Filter chain: ... -> JWT filter -> Security context filter -> ...
     * -> 'Pre' Annotation guards
     * */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilterWithAuthListMatcher(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterChainExceptionHandler, JwtFilter.class)
                .authenticationProvider(daoAuthenticationProvider())
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }


    /** Allow everything from the client to reach the server, cookies included.*/
    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(clientBaseUrl));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "PUT", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("access_token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    JwtFilter jwtFilterWithAuthListMatcher(){
        return new JwtFilter(AUTH_WHITELIST);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
