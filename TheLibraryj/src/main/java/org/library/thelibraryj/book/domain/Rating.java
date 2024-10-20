package org.library.thelibraryj.book.domain;

import jakarta.persistence.*;
import lombok.*;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.ConstraintMode.CONSTRAINT;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "library_ratings")
@NoArgsConstructor
class Rating extends AbstractEntity {
    @Column(nullable = false)
    private int currentRating;
    @Column(nullable = false)
    private UUID userId;

    @Column
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bookDetail_id", foreignKey = @ForeignKey(value = CONSTRAINT, foreignKeyDefinition = "FOREIGN KEY (bookDetail_id) REFERENCES bookDetail(id) ON DELETE CASCADE"))
    private BookDetail bookDetail;

    @Builder
    public Rating(UUID id, Long version, Instant createdAt, Instant updatedAt, int currentRating, UUID userId, String comment, BookDetail bookDetail) {
        super(id, version, createdAt, updatedAt);
        this.currentRating = currentRating;
        this.userId = userId;
        this.comment = comment;
        this.bookDetail = bookDetail;
    }
}
