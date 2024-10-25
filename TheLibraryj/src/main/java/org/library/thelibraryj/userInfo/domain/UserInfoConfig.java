package org.library.thelibraryj.userInfo.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "library.user")
class UserInfoConfig {
    private int username_change_cooldown_days;
    private int minimal_age_hours;
}
