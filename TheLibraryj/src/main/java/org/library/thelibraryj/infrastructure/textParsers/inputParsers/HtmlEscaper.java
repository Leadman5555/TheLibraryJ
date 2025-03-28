package org.library.thelibraryj.infrastructure.textParsers.inputParsers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

/**
 * Most current frontend clients escape HTML themselves during field interpolation (for example, {{}} in Angular).
 * As such, HTML escaping can be disabled depending on client framework - if not, some characters will not be rendered properly in browser.
 * **/
@Component
public class HtmlEscaper {
    private final boolean shouldEscapeHtml;

    public HtmlEscaper(@Value("${library.server-html-escape}") boolean serverHtmlEscape) {
        this.shouldEscapeHtml = serverHtmlEscape;
    }

    public String escapeHtml(String input) {
        if(shouldEscapeHtml) return HtmlUtils.htmlEscape(input);
        return input;
    }
}
