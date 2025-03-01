package org.library.thelibraryj.userInfo.dto.request;

import java.util.List;

public record SubscribedUserNotificationRequest(List<ChapterNotificationData> chapterData, String bookTitle, String bookAuthor, byte[] bookCover, int currentChapterCount) {
    public record ChapterNotificationData(String title, int number){}
}
