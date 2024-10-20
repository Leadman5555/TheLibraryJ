package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface BookDetailRepository extends BaseJpaRepository<BookDetail, UUID> {

    @Query("""
        select bd from bookDetail bd
        left join fetch bd.chapterPreviews
        where bd.id = :id
        """)
    Optional<BookDetail> getBookDetailWithChaptersEager(@Param("id") UUID id);
}
