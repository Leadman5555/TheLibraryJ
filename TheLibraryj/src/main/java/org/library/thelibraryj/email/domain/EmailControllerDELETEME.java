package org.library.thelibraryj.email.domain;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.EmailTemplate;
import org.library.thelibraryj.email.template.AccountActivationTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("${library.mapping}/email")
record EmailControllerDELETEME(EmailService emailService) {

    @Operation(
            description = "I exists only to test Thymeleaf templates",
            tags = "DELETE_ME"
    )
    @PostMapping
    void sendTest() throws MessagingException {
        EmailTemplate template;
        template = new AccountActivationTemplate("user1", "http://localhost:8082/v0.2/books/", Instant.now());
        emailService.sendEmail(new EmailRequest(
                "user1@gmail.com",
                template
        ));
    }
}
