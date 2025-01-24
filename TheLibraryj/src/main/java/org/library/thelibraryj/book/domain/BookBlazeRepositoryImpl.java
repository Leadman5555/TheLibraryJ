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
        cbf.update(em, BookDetail.class)
                .set("author", newUsername)
                .where("authorId").eq(forUserId).executeUpdate();
        cbf.update(em, Rating.class)
                .set("username", newUsername)
                .where("userId").eq(forUserId).executeUpdate();
    }

    @Override
    public PagedList<BookPreview> getKeySetPagedBookPreviewNext(KeysetPage page, int pageNumber) {
        return cbf.create(em, BookPreview.class)
                .orderByDesc("chapterCount")
                .orderByDesc("id")
                .page(page, page.getMaxResults() * pageNumber, page.getMaxResults())
                .withKeysetExtraction(true)
                .getResultList();
    }

    @Override
    public PagedList<BookPreview> getOffsetBookPreviewPaged(int pageSize, int pageNumber) {
        return cbf.create(em, BookPreview.class)
                .orderByDesc("chapterCount")
                .orderByDesc("id")
                .page(pageNumber * pageSize, pageSize)
                .withKeysetExtraction(true)
                .getResultList();
    }

    @Override
    public PagedList<ChapterPreview> getKeySetPagedChapterPreviewNext(KeysetPage page, int pageNumber, UUID bookId) {
        return cbf.create(em, ChapterPreview.class)
                .orderByAsc("number")
                .orderByDesc("id")
                .where("bookDetail.id").eq(bookId)
                .page(page, page.getMaxResults() * pageNumber, page.getMaxResults())
                .withKeysetExtraction(true)
                .getResultList();
    }

    @Override
    public PagedList<ChapterPreview> getOffsetChapterPreviewPaged(int pageSize, int pageNumber, UUID bookId) {
        return cbf.create(em, ChapterPreview.class)
                .orderByAsc("number")
                .orderByDesc("id")
                .where("bookDetail.id").eq(bookId)
                .page(pageNumber * pageSize, pageSize)
                .withKeysetExtraction(true)
                .getResultList();
    }

    @Override
    public List<BookPreview> getBookPreviewByParams(String titleLike, Integer minChapters, Float minRating, BookState state, BookTag[] tags, Boolean ratingOrder) {
        CriteriaBuilder<BookPreview> cb = cbf.create(em, BookPreview.class).from(BookPreview.class);
        if (titleLike != null) cb.whereExpression("title LIKE :titleLike").setParameter("titleLike", titleLike + '%');
        if (minChapters != null) cb.where("chapterCount").ge(minChapters);
        if (minRating != null) cb.where("averageRating").ge(minRating);
        if (state != null) cb.where("bookState").eq(state);
        if (tags != null)
            for (BookTag tag : tags) cb.where(":tag").isMemberOf("bookTags").setParameter("tag", tag);
        if(ratingOrder != null) cb.orderBy("averageRating", ratingOrder);
        return cb.getResultList();
    }

    @Override
    public List<BookPreview> getAuthoredBookPreviews(String byUser){
        return cbf.create(em, BookPreview.class)
                .where("bookDetail.author").eq(byUser)
                .getResultList();
    }
}
