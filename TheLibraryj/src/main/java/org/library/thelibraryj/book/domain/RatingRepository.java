package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface RatingRepository extends BaseJpaRepository<Rating, UUID> {

    @Query("""
    select r from Rating r
    where r.bookDetail.id = :id
    """)
    List<Rating> getAllRatingsForBook(@Param("id") UUID id);

    List<Rating> getAllByBookDetailId(UUID bookDetailId);

    @Query("""
    select r from Rating r
    where r.bookDetail.id == :bookId and r.userId == :userId
    """)
    Optional<Rating> getRatingForBookAndUser(UUID bookId, UUID userId);
}
