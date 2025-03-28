package org.library.thelibraryj.infrastructure.configuration;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TomcatThrottleConfiguration {

    @Bean
    public TomcatServletWebServerFactory servletContainer(@Value("${library.tomcat.throttle_size_bytes}") int throttle_size_bytes) {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> {
            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>) {
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(
                        throttle_size_bytes
                );
            }
        });
        return factory;
    }

}
