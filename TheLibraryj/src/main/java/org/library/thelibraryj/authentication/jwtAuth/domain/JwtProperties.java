package org.library.thelibraryj.authentication.jwtAuth.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "library.auth.jwt")
@Configuration
@Data
class JwtProperties {
    private String client_id;
    private String aud;
    private int expiration_time_ms;
    private int expiration_time_ms_refresh;
    private String refresh_domain;
    private boolean send_secure;
}
