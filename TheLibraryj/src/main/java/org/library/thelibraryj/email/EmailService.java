package org.library.thelibraryj.email;

import jakarta.mail.MessagingException;
import org.library.thelibraryj.email.dto.EmailRequest;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendEmail(EmailRequest emailRequest) throws MessagingException;
}
