package org.library.thelibraryj.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "rating")
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "library_ratings")
@NoArgsConstructor
class Rating extends AbstractEntity {
    @Column(nullable = false)
    private int currentRating;
    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String username;
    @Column
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_detail_id")
    private BookDetail bookDetail;

    @Builder
    public Rating(UUID id, Long version, Instant createdAt, Instant updatedAt, int currentRating, UUID userId, String comment, BookDetail bookDetail, String username) {
        super(id, version, createdAt, updatedAt);
        this.currentRating = currentRating;
        this.userId = userId;
        this.comment = comment;
        this.bookDetail = bookDetail;
        this.username = username;
    }
}
