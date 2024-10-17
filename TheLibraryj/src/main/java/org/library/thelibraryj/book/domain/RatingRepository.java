package org.library.thelibraryj.book.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface RatingRepository extends JpaRepository<Rating, UUID> {
}
