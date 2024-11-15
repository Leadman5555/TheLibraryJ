package org.library.thelibraryj.authentication.googleAuth.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.opaque-token")
class GoogleAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private UUID default_google_id;
}
