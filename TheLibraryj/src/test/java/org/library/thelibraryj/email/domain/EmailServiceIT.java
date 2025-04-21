package org.library.thelibraryj.email.domain;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.library.thelibraryj.TestProperties;
import org.library.thelibraryj.email.EmailService;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.library.thelibraryj.email.template.AccountActivationTemplate;
import org.library.thelibraryj.email.template.EmailTemplate;
import org.library.thelibraryj.email.template.FavouriteBookTokenTemplate;
import org.library.thelibraryj.email.template.PasswordResetTemplate;
import org.library.thelibraryj.email.template.SubscribedBookNotificationTemplate;
import org.library.thelibraryj.userInfo.dto.request.SubscribedUserNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(value = {EmailServiceImpl.class, EmailConfiguration.class, EmailProperties.class})
public class EmailServiceIT {

    @Autowired
    private EmailService emailService;

    @RegisterExtension
    protected static final GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("username", "password"))
            .withPerMethodLifecycle(false);

    @Autowired
    private MockMvc mockMvc;

    private static final String URL_BASE = TestProperties.BASE_URL + "/email";

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCheckConnection() throws Exception {
        mockMvc.perform(post(URL_BASE + "/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void renderTemplatesAndSendMails() throws Exception {
        Random random = new Random();
        List<EmailTemplate> templateList = List.of(
                new AccountActivationTemplate(
                        "sample username", "sample link", Instant.now().plusSeconds(random.nextInt(10000))
                ),
                new PasswordResetTemplate("sample link", Instant.now().plusSeconds(random.nextInt(10000))),
                new FavouriteBookTokenTemplate(UUID.randomUUID(), random.nextInt(100), Instant.now().plusSeconds(random.nextInt(10000))),
                new SubscribedBookNotificationTemplate(new SubscribedUserNotificationRequest(new ArrayList<>(), "sample", "sample", new byte[1], 10))
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
