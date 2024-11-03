package org.library.thelibraryj.email.template;

public sealed interface EmailTemplateInterface permits EmailTemplate {
    String getSubject();
}
