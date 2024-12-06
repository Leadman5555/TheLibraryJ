package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;

import java.util.UUID;

@EntityView(ChapterPreview.class)
public interface ChapterPreviewTitleView {
    @IdMapping
    UUID getId();
    String getTitle();
}
