package org.library.thelibraryj.book.domain;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.KeysetPage;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.UpdateCriteriaBuilder;
import jakarta.persistence.EntityManager;
import org.library.thelibraryj.infrastructure.model.blaze.BlazeRepositoryBase;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
class BookBlazeRepositoryImpl extends BlazeRepositoryBase implements BookBlazeRepository {
    public BookBlazeRepositoryImpl(EntityManager em, CriteriaBuilderFactory cbf) {
        super(em, cbf);
    }

    private final UpdateCriteriaBuilder<BookDetail> bookDetailUsernameUpdateCriteria = cbf.update(em, BookDetail.class)
            .where("authorId").eqExpression(":userId")
            .setExpression("author", ":username");

    private final UpdateCriteriaBuilder<Rating> ratingUsernameUpdateCriteria = cbf.update(em, Rating.class)
            .where("userId").eqExpression(":userId")
            .setExpression("username", ":username");

    @Override
    public void updateAllForNewUsername(UUID forUserId, String newUsername) {
        bookDetailUsernameUpdateCriteria.setParameter("userId", forUserId).setParameter("username", newUsername).executeUpdate();
        ratingUsernameUpdateCriteria.setParameter("userId", forUserId).setParameter("username", newUsername).executeUpdate();
    }

    private final CriteriaBuilder<BookPreview> pagedSetupBookPreviewPagedCriteria = cbf.create(em, BookPreview.class)
            .orderByDesc("chapterCount")
            .orderByDesc("id");

    @Override
    public PagedList<BookPreview> getKeySetPagedBookPreviewNext(KeysetPage page, int pageNumber) {
        return pagedSetupBookPreviewPagedCriteria
                .page(page, page.getMaxResults() * pageNumber, page.getMaxResults())
                .withKeysetExtraction(true)
                .getResultList();
    }

    @Override
    public PagedList<BookPreview> getOffsetBookPreviewPaged(int pageSize, int pageNumber) {
        return pagedSetupBookPreviewPagedCriteria
                .page(pageNumber * pageSize, pageSize)
                .withKeysetExtraction(true)
                .getResultList();
    }

    @Override
    public PagedList<ChapterPreview> getKeySetPagedChapterPreviewNext(KeysetPage page, int pageNumber, UUID bookId) {
        return pagedSetupChapterPreviewPagedCriteria.setParameter("bookId", bookId)
                .page(page, page.getMaxResults() * pageNumber, page.getMaxResults())
                .withKeysetExtraction(true)
                .getResultList();
    }

    private final CriteriaBuilder<ChapterPreview> pagedSetupChapterPreviewPagedCriteria = cbf.create(em, ChapterPreview.class)
            .orderByAsc("number")
            .orderByDesc("id")
            .where("bookDetail.id").eqExpression(":bookId");

    @Override
    public PagedList<ChapterPreview> getOffsetChapterPreviewPaged(int pageSize, int pageNumber, UUID bookId) {
        return pagedSetupChapterPreviewPagedCriteria.setParameter("bookId", bookId)
                .page(pageNumber * pageSize, pageSize)
                .withKeysetExtraction(true)
                .getResultList();
    }

    @Override
    public PagedList<BookPreview> getKeySetPagedBookPreviewByParams(String titleLike, Integer minChapters, Float minRating, BookState state, BookTag[] tags, Boolean sortAscByRating, KeysetPage page, int pageNumber) {
        return createBookPreviewParamCB(titleLike, minChapters, minRating, state, tags, sortAscByRating)
                .page(page, page.getMaxResults() * pageNumber, page.getMaxResults())
                .withKeysetExtraction(true)
                .getResultList();
    }

    @Override
    public PagedList<BookPreview> getOffsetBookPreviewByParams(String titleLike, Integer minChapters, Float minRating, BookState state, BookTag[] tags, Boolean sortAscByRating, int pageSize, int pageNumber) {
        return createBookPreviewParamCB(titleLike, minChapters, minRating, state, tags, sortAscByRating)
                .page(pageNumber * pageSize, pageSize)
                .withKeysetExtraction(true)
                .getResultList();
    }

    private CriteriaBuilder<BookPreview> createBookPreviewParamCB(String titleLike, Integer minChapters, Float minRating, BookState state, BookTag[] tags, Boolean sortAscByRating) {
        CriteriaBuilder<BookPreview> cb = cbf.create(em, BookPreview.class);
        if (titleLike != null) cb.whereExpression("title LIKE :titleLike").setParameter("titleLike", titleLike + '%');
        if (minChapters != null) cb.where("chapterCount").ge(minChapters);
        if (minRating != null) cb.where("averageRating").ge(minRating);
        if (state != null) cb.where("bookState").eq(state);
        if (tags != null)
            for (int i = 0; i < tags.length; i++){
                String paramName = "tag_" + i;
                cb.where(':' + paramName).isMemberOf("bookTags").setParameter(paramName, tags[i]);
            }
        if (sortAscByRating != null) cb.orderBy("averageRating", sortAscByRating).orderByDesc("id");
        else cb.orderByDesc("chapterCount").orderByDesc("id");
        return cb;
    }

    private final CriteriaBuilder<BookPreview> authoredBookPreviewsCriteria = cbf.create(em, BookPreview.class)
            .where("bookDetail.author").eqExpression(":byUser");

    @Override
    public List<BookPreview> getAuthoredBookPreviews(String byUser) {
        return authoredBookPreviewsCriteria.setParameter("byUser", byUser).getResultList();
    }

    private final CriteriaBuilder<ChapterPreview> sortedChapterPreviewsCriteria = cbf.create(em, ChapterPreview.class)
            .orderByAsc("id")
            .where("bookDetail.id").eqExpression(":bookId")
            .where("number").inExpressions(":chapterNumbers");

    @Override
    public List<ChapterPreview> getSortedChapterPreviews(UUID bookId, Set<Integer> chapterNumbers) {
        return sortedChapterPreviewsCriteria.setParameter("bookId", bookId)
                .setParameter("chapterNumbers", chapterNumbers)
                .getResultList();
    }

    private final CriteriaBuilder<Chapter> sortedChapterCriteria = cbf.create(em, Chapter.class)
            .orderByAsc("id")
            .where("id").inExpressions(":chapterPreviewIds");

    @Override
    public List<Chapter> getSortedChapters(Set<UUID> chapterPreviewIds) {
        return sortedChapterCriteria.setParameter("chapterPreviewIds", chapterPreviewIds)
                .getResultList();
    }
}
