package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.view.EntityView;

@EntityView(BookPreview.class)
public interface NotificationEssentialsView {
    String getTitle();
    int getChapterCount();
}
