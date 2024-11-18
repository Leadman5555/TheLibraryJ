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
interface RatingRepository extends BaseJpaRepository<Rating, UUID> {

    @Query("""
    select r from rating r
    where r.bookDetail.id = :id
    """)
    List<Rating> getAllRatingsForBook(@Param("id") UUID id);


    @Query("""
    select r from rating r
    where r.userId = :userId and r.bookDetail.id = :bookId
    """)
    Optional<Rating> getRatingForBookAndUser(@Param("bookId") UUID bookId, @Param("userId") UUID userId);

    @Modifying
    @Query("""
    delete from rating
    where bookDetail.id = :bookId
    """)
    void deleteBook(UUID bookId);

    List<Rating> getRatingsByUserId(UUID userId);
}
