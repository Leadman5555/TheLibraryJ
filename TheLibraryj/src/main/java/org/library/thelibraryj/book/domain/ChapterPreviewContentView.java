package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;

import java.util.UUID;

@EntityView(ChapterPreview.class)
public interface ChapterPreviewContentView {
    @IdMapping
    UUID getId();
    String getTitle();
    @Mapping("isSpoiler")
    boolean isSpoiler();
}
