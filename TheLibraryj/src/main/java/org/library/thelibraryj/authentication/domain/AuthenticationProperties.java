package org.library.thelibraryj.authentication.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "library.auth")
@Getter
@Setter
public class AuthenticationProperties {
    private String activation_link;
}
