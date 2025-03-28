package org.library.thelibraryj.email.dto;

import org.library.thelibraryj.email.template.EmailTemplate;

public record EmailRequest(String recipient, EmailTemplate template) { }
