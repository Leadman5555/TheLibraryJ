package org.library.thelibraryj.email.template;

import org.library.thelibraryj.userInfo.dto.request.SubscribedUserNotificationRequest;

import java.util.Map;

public final class SubscribedBookNotificationTemplate extends EmailTemplate {
    public SubscribedBookNotificationTemplate(SubscribedUserNotificationRequest request) {
        super("subscribed-book-notification-email-template.html");
        final String imageString = new String(request.bookCover());
        StringBuilder formattedString = new StringBuilder();
        request.chapterData().forEach(notificationData ->
                formattedString
                        .append(notificationData.number())
                        .append(" - ")
                        .append(notificationData.title())
                        .append('\n')
        );
        parameters = Map.of(
                "subject", "Subscribed book notification",
                "book_author", request.bookAuthor(),
                "book_title", request.bookTitle(),
                "image_string", imageString,
                "chapter_count", request.currentChapterCount(),
                "chapter_data", formattedString.toString()
        );
    }
}
