package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface BookDetailRepository extends BaseJpaRepository<BookDetail, UUID> {

    @Query("""
        select bd.authorId from bookDetail bd
        where bd.id = :bookId
    """)
    Optional<UUID> getAuthorId(@Param("bookId") UUID bookId);

    List<BookDetail> getBookDetailByAuthorId(UUID authorId);
}
