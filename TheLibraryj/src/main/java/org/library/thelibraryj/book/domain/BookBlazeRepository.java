package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;

import java.util.UUID;

interface BookBlazeRepository {
    void updateAllForNewUsername(UUID forUserId, String newUsername);
    PagedList<BookPreview> getKeySetPagedNext(KeysetPage page, int pageNumber);
    PagedList<BookPreview> getOffsetPaged(int pageSize, int pageNumber);
}
