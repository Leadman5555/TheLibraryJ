package org.library.thelibraryj.userInfo.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "library.user")
class UserInfoProperties {
    private int username_change_cooldown_days;
    private int minimal_age_hours;
    private int points_for_comment;
    private int points_for_review;
    private int points_for_author;
    private String rank_requirements;
    private top_rated top_rated;

    @Data
    static class top_rated {
        private int limit;
        private String cache_evict_hours_list;
    }
}
