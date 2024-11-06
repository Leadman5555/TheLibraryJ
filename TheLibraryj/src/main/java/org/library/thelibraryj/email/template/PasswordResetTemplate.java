package org.library.thelibraryj.email.template;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public final class PasswordResetTemplate extends EmailTemplate{
    public PasswordResetTemplate(String passwordResetLink, Instant expiresAt) {
        super("password-reset-email-template.html");
        final String imageName = "password_reset.jpg";
        parameters = Map.of(
                "subject", "Immortal Cave entry code reset",
                "password_reset_link", passwordResetLink,
                "image_name", imageName,
                "expires_at", ChronoUnit.HOURS.between(Instant.now(), expiresAt) + " hours"
        );
    }
}
