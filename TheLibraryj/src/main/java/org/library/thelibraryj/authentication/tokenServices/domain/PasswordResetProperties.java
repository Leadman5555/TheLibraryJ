package org.library.thelibraryj.authentication.tokenServices.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "library.password")
@Getter
@Setter
class PasswordResetProperties {
    private String activation_link;
    private long expiration_time_seconds;
}