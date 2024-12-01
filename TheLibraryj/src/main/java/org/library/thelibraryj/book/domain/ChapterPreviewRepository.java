package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface ChapterPreviewRepository extends BaseJpaRepository<ChapterPreview, UUID> {
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
    select title, id from  chapterPreview
    where bookDetail.id = :bookId and number = :number
""")
    Optional<Object> findChapterPreviewTitleAndIdByBookIdAndNumber(@Param("bookId") UUID bookId, @Param("number") int number);

    @Query("""
    select (count(c) > 0) from chapterPreview c
    where c.bookDetail.id = :bookId and c.number = :number
""")
    boolean existsByBookIdAndNumber(@Param("bookId") UUID bookId, @Param("number") int number);

    @Modifying
    @Query("""
    delete from chapterPreview
    where bookDetail.id = :bookId
    """)
    void deleteBook(UUID bookId);
}
