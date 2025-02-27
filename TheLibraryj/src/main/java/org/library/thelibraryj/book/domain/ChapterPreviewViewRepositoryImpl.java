package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import org.library.thelibraryj.infrastructure.model.blaze.ViewRepositoryBase;

import java.util.UUID;

class ChapterPreviewViewRepositoryImpl extends ViewRepositoryBase implements ChapterPreviewViewRepository {

    public ChapterPreviewViewRepositoryImpl(EntityManager entityManager, CriteriaBuilderFactory builderFactory, EntityViewManager viewManager) {
        super(entityManager, builderFactory, viewManager);
    }

    @Override
    public ChapterPreviewTitleView findChapterPreviewTitleAndIdByBookIdAndNumber(UUID bookId, int number) {
        return evm.applySetting(EntityViewSetting.create(ChapterPreviewTitleView.class),
                        cbf.create(em, ChapterPreview.class, "c")
                                .where("c.number").eq(number)
                                .where("c.bookDetail.id").eq(bookId))
                .getSingleResult();
    }
}
