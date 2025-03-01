package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import org.library.thelibraryj.infrastructure.model.blaze.ViewRepositoryBase;

import java.util.UUID;

class BookViewRepositoryImpl extends ViewRepositoryBase implements BookViewRepository {

    public BookViewRepositoryImpl(EntityManager entityManager, CriteriaBuilderFactory builderFactory, EntityViewManager viewManager) {
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

    @Override
    public NotificationEssentialsView findNotificationEssentialsViewByBookId(UUID bookId) {
        return evm.applySetting(EntityViewSetting.create(NotificationEssentialsView.class),
                        cbf.create(em, BookPreview.class)
                                .where("id").eq(bookId))
                .getSingleResult();
    }
}
