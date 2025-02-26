package org.library.thelibraryj.email.template;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

public final class FavouriteBookTokenTemplate extends EmailTemplate {
    public FavouriteBookTokenTemplate(UUID token, int useCount, Instant expiresAt) {
        super("book-token-email-template.html");
        final String imageName = "jade_slip.png";
        parameters = Map.of(
                "subject", "Favourite book token",
                "token", token.toString(),
                "image_name", imageName,
                "use_count", useCount == 1 ? "one time" : useCount + " times",
                "expires_at", ChronoUnit.HOURS.between(Instant.now(), expiresAt) + " hours"
        );
    }
}
