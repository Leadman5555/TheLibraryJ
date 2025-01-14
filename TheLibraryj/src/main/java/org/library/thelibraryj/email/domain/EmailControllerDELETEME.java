package org.library.thelibraryj.email.domain;

import io.swagger.v3.oas.annotations.Operation;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.AccountActivationTemplate;
import org.library.thelibraryj.email.template.EmailTemplate;
import org.library.thelibraryj.email.template.PasswordResetTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("${library.mapping}/email")
record EmailControllerDELETEME(EmailService emailService) {

    @Operation(
            description = "I exists only to test Thymeleaf email templates. Enter the template name to test. Sends to 'user1@gmail.com'",
            tags = "DELETE_ME"
    )
    @PostMapping("/{templateName}")
    void sendTest(@PathVariable("templateName") String templateName) {
        EmailTemplate template;
        Instant expiresAt = Instant.now().plusSeconds(24*60*6);
        switch (templateName) {
            case "account-activation": template = new AccountActivationTemplate("user1", "SOME-LINK", expiresAt); break;
            case "password-reset": template = new PasswordResetTemplate("SOME-LINK", expiresAt); break;
            default: return;
        }
        emailService.sendEmail(new EmailRequest(
                "user1@gmail.com",
                template
        ));
    }
}
