package org.library.thelibraryj.email.dto;

public record EmailRequest(String subject, String recipient, EmailTemplate template) { }
