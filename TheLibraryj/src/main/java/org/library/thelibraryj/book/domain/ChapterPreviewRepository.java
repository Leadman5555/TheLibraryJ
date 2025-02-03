package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
interface ChapterPreviewRepository extends BaseJpaRepository<ChapterPreview, UUID>, ChapterPreviewViewRepository {
    @Query("""
            select c from  chapterPreview c
            where c.bookDetail.id = :id
            order by c.number asc
            """)
    List<ChapterPreview> getAllChapterPreviewsForBook(@Param("id") UUID id);

    @Query("""
                select id from  chapterPreview
                where bookDetail.id = :bookId and number = :number
            """)
    Optional<UUID> findChapterPreviewIdByBookIdAndNumber(@Param("bookId") UUID bookId, @Param("number") int number);

    @Query("""
                select chapterPreview from chapterPreview
                where bookDetail.id = :bookId and number = :number
            """)
    Optional<ChapterPreview> findChapterPreview(@Param("bookId") UUID bookId, @Param("number") int number);

    @Query("""
                select chapterPreview from chapterPreview
                where bookDetail.id = :bookId and number in :chapterNumbers
            """)
    List<ChapterPreview> fetchPreviewsByBookIdAndNumber(@Param("bookId") UUID bookId, @Param("chapterNumbers") Set<Integer> chapterNumbers);

    @Modifying
    @Query("""
                delete from chapterPreview
                where bookDetail.id = :bookId and number = :number
            """)
    void deleteChapter(@Param("bookId") UUID bookId, @Param("number") int number);


    @Modifying
    @Query("""
            delete from chapterPreview
            where bookDetail.id = :bookId
            """)
    void deleteBook(UUID bookId);

    @Modifying
    @Query("""
                delete from chapterPreview
                where bookDetail.id = :bookId and number in :chapterNumbers
            """)
    void deleteChapters(@Param("bookId") UUID bookId, @Param("chapterNumbers") Set<Integer> chapterNumbers);
}
