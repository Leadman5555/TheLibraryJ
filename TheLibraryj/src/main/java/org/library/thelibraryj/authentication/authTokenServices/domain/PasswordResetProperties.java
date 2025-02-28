package org.library.thelibraryj.authentication.authTokenServices.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "library.auth.password")
@Getter
@Setter
class PasswordResetProperties {
    private String activation_link;
    private long expiration_time_seconds;
}