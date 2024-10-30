package org.library.thelibraryj.email.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class EmailTemplate {

    private final String templateName;
    protected Map<String, Object> parameters;

    public EmailTemplate(String templateName) {
        this.templateName = templateName;
    }

    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }
}
