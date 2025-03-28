package org.library.thelibraryj.email.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@RequiredArgsConstructor
class EmailConfiguration {
    private final EmailProperties emailProperties;

    @Bean
    protected JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setPort(emailProperties.getPort());
        sender.setHost(emailProperties.getHost());
        sender.setPassword(emailProperties.getPassword());
        sender.setUsername(emailProperties.getUsername());
        return sender;
    }

    @Bean
    public SpringResourceTemplateResolver setUpEmailTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/email/");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setSuffix(".html");
        resolver.setCacheable(false);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine emailTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(setUpEmailTemplateResolver());
        engine.setEnableSpringELCompiler(true);
        return engine;
    }
}
