package org.library.thelibraryj.email.template;

import java.util.Map;

public final class AccountActivationTemplate extends EmailTemplate {

    public AccountActivationTemplate(String username, String activationLink) {
        super("activation-email-template.html");
        final String imageName = "account_activation.jpg";
        parameters = Map.of(
                "subject", "Recruitment confirmation",
                "username", username,
                "activation_link", activationLink,
                "image_name", imageName
        );
    }
}
