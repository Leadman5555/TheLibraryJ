package org.library.thelibraryj.infrastructure.imageHandling;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "library.image")
public class ImageHandlerProperties {
    private String base;
    private String mapping;
    private String endpoint_domain;
}