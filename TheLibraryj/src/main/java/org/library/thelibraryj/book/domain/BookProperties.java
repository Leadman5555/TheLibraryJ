package org.library.thelibraryj.book.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "library.book")
class BookProperties {
    private int chapter_max_length;
    private int chapter_max_number;
    private int description_max_length;
}

