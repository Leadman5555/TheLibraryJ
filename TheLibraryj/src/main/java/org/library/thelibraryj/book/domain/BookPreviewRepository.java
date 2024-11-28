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
interface BookPreviewRepository extends BaseJpaRepository<BookPreview, UUID> {
    Optional<BookPreview> findByTitle(String title);

    @Query("""
                select bp from bookPreview bp
                join fetch bp.bookTags
            """)
    List<BookPreview> getAllBookPreviewsEager();

    @Query("""
            select bp from bookPreview bp
            join fetch bp.bookTags
            where bp.id = :id
            """)
    Optional<BookPreview> getBookPreviewEager(@Param("id") UUID id);

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
}
