package org.library.thelibraryj.email.template;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public final class AccountActivationTemplate extends EmailTemplate {

    public AccountActivationTemplate(String username, String activationLink, Instant expiresAt) {
        super("activation-email-template.html");
        final String imageName = "account_activation.jpg";
        parameters = Map.of(
                "subject", "Recruitment confirmation",
                "username", username,
                "activation_link", activationLink,
                "image_name", imageName,
                "expires_at", ChronoUnit.HOURS.between(Instant.now(), expiresAt) + " hours"
        );
    }
}
