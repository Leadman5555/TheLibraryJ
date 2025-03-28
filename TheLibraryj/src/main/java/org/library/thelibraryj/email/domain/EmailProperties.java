package org.library.thelibraryj.email.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "library.email")
class EmailProperties {
    private String host;
    private Integer port;
    private String username;
    private String password;
}
