package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import jakarta.persistence.EntityManager;
import org.library.thelibraryj.infrastructure.model.BlazeRepositoryBase;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Override
    public List<BookPreview> getByParams(String titleLike, Integer minChapters, Float minRating, BookState state, BookTag[] tags) {
        CriteriaBuilder<BookPreview> cb = cbf.create(em, BookPreview.class).from(BookPreview.class, "bp");
        if(titleLike != null) cb.whereExpression("bp.title LIKE :titleLike").setParameter("titleLike", titleLike+'%');
        if(minChapters != null) cb.where("bp.chapterCount").ge(minChapters);
        if(minRating != null) cb.where("bp.averageRating").ge(minRating);
        if(state != null) cb.where("bp.bookState").eq(state);
        if(tags != null) {
            for (BookTag tag : tags) cb.where(":tag").isMemberOf("bp.bookTags").setParameter("tag", tag);

        }
        return cb.getResultList();
    }
}
