package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
interface BookPreviewRepository extends BaseJpaRepository<BookPreview, UUID> {
    @Query("""
            select bp from bookPreview bp
            join fetch bp.bookTags
            where bp.title = :title
            """)
    Optional<BookPreview> findByTitleEager(@Param("title") String title);

    @Query("""
            select bp from bookPreview bp
            join fetch bp.bookTags
            where bp.id = :id
            """)
    Optional<BookPreview> getBookPreviewEager(@Param("id") UUID id);

    @Query("""
            select bp from bookPreview bp
            join fetch bp.bookTags
            where bp.id in (:idSet)
            """)
    Set<BookPreview> getBookPreviewsEagerByIds(@Param("idSet") Set<UUID> idSet);

    @Query("""
            select (count(b) > 0) from bookPreview b where b.title = :title
            """)
    boolean existsByTitle(@Param("title") String title);

    @Modifying
    @Query("""
            delete from bookPreview
            where id = :bookId
            """)
    void deleteBook(@Param("bookId") UUID bookId);

    @Modifying
    @Query("""
                    update bookPreview  bp SET bp.chapterCount = (bp.chapterCount -1)
                    where bp.id = :bookId
            """)
    void decrementChapterCount(@Param("bookId") UUID bookId);

    @Modifying
    @Query("""
                    update bookPreview  bp SET bp.chapterCount = (bp.chapterCount + :change)
                    where bp.id = :bookId
            """)
    void changeChapterCount(@Param("bookId") UUID bookId, @Param("change") int change);
}
