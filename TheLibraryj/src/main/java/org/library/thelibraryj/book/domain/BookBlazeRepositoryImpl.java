package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import jakarta.persistence.EntityManager;
import org.library.thelibraryj.infrastructure.model.BlazeRepositoryBase;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
class BookBlazeRepositoryImpl extends BlazeRepositoryBase implements BookBlazeRepository {
    public BookBlazeRepositoryImpl(EntityManager em, CriteriaBuilderFactory cbf) {
        super(em, cbf);
    }

    @Override
    public void updateAllForNewUsername(UUID forUserId, String newUsername) {
        cbf.update(em, BookDetail.class, "bd")
                .set("author", newUsername)
                .where("bd.authorId").eq(forUserId);
        cbf.update(em, Rating.class, "r")
                .set("username", newUsername)
                .where("r.userId").eq(forUserId);
    }

    @Override
    public PagedList<BookPreview> getKeySetPagedNext(KeysetPage page, int pageNumber) {
        return cbf.create(em, BookPreview.class, "bp")
                .orderByDesc("bp.chapterCount")
                .orderByDesc("bp.id")
                .page(page, page.getMaxResults()*pageNumber, page.getMaxResults())
                .withKeysetExtraction(true)
                .getResultList();
    }

    @Override
    public PagedList<BookPreview> getOffsetPaged(int pageSize, int pageNumber) {
        return cbf.create(em, BookPreview.class, "bp")
                .orderByDesc("bp.chapterCount")
                .orderByDesc("bp.id")
                .page(pageNumber*pageSize, pageSize)
                .withKeysetExtraction(true)
                .getResultList();
    }
}
