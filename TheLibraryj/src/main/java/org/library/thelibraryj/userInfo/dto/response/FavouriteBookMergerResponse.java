package org.library.thelibraryj.userInfo.dto.response;

public record FavouriteBookMergerResponse(int sizeBeforeMerge, int sizeAfterMerge, int attemptedToMergeCount, String fromUsername, String toUsername) {
}
