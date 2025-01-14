package org.library.thelibraryj.email.domain;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;


@Service
@Slf4j
class EmailServiceImpl implements org.library.thelibraryj.email.EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine emailTemplateEngine;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine emailTemplateEngine) {
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
    }

    @Async
    @Override
    public void sendEmail(EmailRequest emailRequest) {
        final Context ctx = new Context(Locale.ENGLISH);
        Map<String, Object> params = emailRequest.template().getParameters();
        params.forEach(ctx::setVariable);
        final String renderedHtml = emailTemplateEngine.process(emailRequest.template().getTemplateName(), ctx);
        final MimeMessage mailToSend = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(mailToSend, true, "UTF-8");
            helper.setTo(emailRequest.recipient());
            helper.setSubject(emailRequest.template().getSubject());
            helper.setText(renderedHtml, true);
            Object imageQuestion = params.get("image_name");
            if (imageQuestion != null) {
                String imageName = imageQuestion.toString();
                helper.addInline(imageName, new ClassPathResource("/templates/email/images/" + imageName), "image/jpg");
            }
            mailSender.send(mailToSend);
        }catch (MailException | MessagingException e){
            log.error("Error sending mail: {}", e.getMessage());
        }
    }
}
