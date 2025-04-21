package org.library.thelibraryj.email.domain;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@RequiredArgsConstructor
@Slf4j
class EmailConfiguration {

    private final EmailProperties emailProperties;

    @Bean
    protected JavaMailSender getJavaMailSender() throws MessagingException {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setPort(emailProperties.getPort());
        sender.setHost(emailProperties.getHost());
        sender.setPassword(emailProperties.getPassword());
        sender.setUsername(emailProperties.getUsername());
        sender.getJavaMailProperties().setProperty("mail.smtp.port", emailProperties.getPort().toString());
        Transport transport = sender.getSession().getTransport("smtp");
        transport.connect(
                emailProperties.getHost(),
                emailProperties.getUsername(),
                emailProperties.getPassword()
        );
        log.info("Successfully connected to email server on port {} with username {}", emailProperties.getPort(), emailProperties.getUsername());
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
