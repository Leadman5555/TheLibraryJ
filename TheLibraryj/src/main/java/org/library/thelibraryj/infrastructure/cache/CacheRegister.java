package org.library.thelibraryj.infrastructure.cache;

import java.util.List;

public class CacheRegister {

    public static final String BOOK_PREVIEW_OFFSET_CACHE = "bookPreviewsOffset";
    public static final String BOOK_PREVIEW_KEYSET_CACHE = "bookPreviewsKeySet";
    public static final String CHAPTER_PREVIEW_OFFSET_CACHE = "chapterPreviewOffset";
    public static final String TOP_USERS_CACHE = "topUsers";
    public static final List<String> CACHE_NAMES = List.of(BOOK_PREVIEW_OFFSET_CACHE, BOOK_PREVIEW_KEYSET_CACHE, CHAPTER_PREVIEW_OFFSET_CACHE, TOP_USERS_CACHE);

}
