package org.library.thelibraryj.authentication.jwtAuth.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "library.auth.jwt")
@Configuration
@Data
class JwtProperties {
    private String secret_key;
    private int expiration_time_ms;
}
