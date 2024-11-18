package org.library.thelibraryj.infrastructure.configuration;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TomcatThrottleConfig {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        final int throttleSizeMb = 2 * 1024 * 1024;
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> {
            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>) {
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(
                        throttleSizeMb
                );
            }
        });
        return factory;
    }

}
