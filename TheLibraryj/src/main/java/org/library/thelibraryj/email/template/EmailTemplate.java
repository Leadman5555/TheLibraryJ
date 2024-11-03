package org.library.thelibraryj.email.template;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract sealed class EmailTemplate implements EmailTemplateInterface permits AccountActivationTemplate{

    private final String templateName;
    protected Map<String, Object> parameters;

    public EmailTemplate(String templateName) {
        this.templateName = templateName;
    }

    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    @Override
    public String getSubject() {
        return parameters.get("subject").toString();
    }
}
