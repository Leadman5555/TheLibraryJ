package org.library.thelibraryj.email.domain;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.dto.templates.AccountActivationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmailServiceTest {
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "password"))
            .withPerMethodLifecycle(false);

    @Autowired
    private EmailService emailService;

    @Test
    public void sendConfirmationMail() throws Exception {
        final String recipient = "recipient@example.com";
        final String subject = "Account Activation";
        EmailRequest request = new EmailRequest(subject, recipient, new AccountActivationTemplate(
                "sample username", "sample link"
        ));

        emailService.sendEmail(request);

        await().atMost(10, TimeUnit.SECONDS).until(
                () -> greenMail.getReceivedMessagesForDomain(recipient).length == 2
        );
        final MimeMessage[] receivedMessages = greenMail.getReceivedMessagesForDomain(recipient);
        assertAll(
                () -> assertEquals(2, receivedMessages.length),
                () -> assertEquals(subject, receivedMessages[0].getSubject()),
                () -> assertEquals(receivedMessages[0].getContent(), receivedMessages[1].getContent()));
    }

}
