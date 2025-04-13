package org.library.thelibraryj.email.domain;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.TestContextInitialization;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.AccountActivationTemplate;
import org.library.thelibraryj.email.template.EmailTemplate;
import org.library.thelibraryj.email.template.FavouriteBookTokenTemplate;
import org.library.thelibraryj.email.template.PasswordResetTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmailServiceTest extends TestContextInitialization {

    @Autowired
    private EmailService emailService;

    @Test
    public void renderTemplatesAndSendMails() throws Exception {
        Random random = new Random();
        List<EmailTemplate> templateList = List.of(
                new AccountActivationTemplate(
                        "sample username", "sample link", Instant.now().plusSeconds(random.nextInt(10000))
                ),
                new PasswordResetTemplate("sample link", Instant.now().plusSeconds(random.nextInt(10000))),
                new FavouriteBookTokenTemplate(UUID.randomUUID(), random.nextInt(100), Instant.now().plusSeconds(random.nextInt(10000)))
        );
        final String recipient = "recipient@example.com";
        int currentEmailCount = 0;
        for (EmailTemplate template : templateList) {
            final String subject = template.getSubject();
            EmailRequest request = new EmailRequest(recipient, template);

            emailService.sendEmail(request);

            int finalCurrentEmailCount = ++currentEmailCount;
            await().atMost(5, TimeUnit.SECONDS).until(
                    () -> greenMail.getReceivedMessagesForDomain(recipient).length == finalCurrentEmailCount
            );
            final MimeMessage[] receivedMessages = greenMail.getReceivedMessagesForDomain(recipient);
            assertEquals(finalCurrentEmailCount, receivedMessages.length);
            assertEquals(subject, receivedMessages[finalCurrentEmailCount-1].getSubject());
        }
    }

}
