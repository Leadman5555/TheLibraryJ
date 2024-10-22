package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
            select bd from bookPreview bd
            join fetch bd.bookTags
            where bd.id = :id
            """)
    Optional<BookPreview> getBookPreviewEager(UUID id);

    @Query("""
    select (count(b) > 0) from bookPreview b where b.title = :title
    """)
    boolean existsByTitle(String title);

    @Modifying
    @Query("""
    delete from bookPreview
    where id = :bookId
    """)
    void deleteBook(UUID bookId);
}
