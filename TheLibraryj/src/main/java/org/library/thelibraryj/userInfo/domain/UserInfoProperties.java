package org.library.thelibraryj.userInfo.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

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
    private int[] rank_requirements_array = Arrays.stream(rank_requirements.split(","))
            .map(String::trim).mapToInt(Integer::parseInt).toArray();
    private int rank_count = rank_requirements_array.length;
}
