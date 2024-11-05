package org.library.thelibraryj.email.domain;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.EmailTemplate;
import org.library.thelibraryj.email.template.AccountActivationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmailServiceTest {
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("username", "password"))
            .withPerMethodLifecycle(false);

    @Autowired
    private EmailService emailService;

    @Test
    public void sendConfirmationMail() throws Exception {;
        final String recipient = "recipient@example.com";
        EmailTemplate template = new AccountActivationTemplate(
                "sample username", "sample link", Instant.now()
        );
        final String subject = template.getSubject();
        EmailRequest request = new EmailRequest(recipient, template);

        final int times = 2;
        for(int i = 0; i < times; i++) emailService.sendEmail(request);

        await().atMost(10, TimeUnit.SECONDS).until(
                () -> greenMail.getReceivedMessagesForDomain(recipient).length == times
        );
        final MimeMessage[] receivedMessages = greenMail.getReceivedMessagesForDomain(recipient);
        assertEquals(2, receivedMessages.length);
        assertEquals(subject, receivedMessages[0].getSubject());
    }

}
