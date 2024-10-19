package org.library.thelibraryj.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Table(name = "library_ratings")
@NoArgsConstructor
class Rating extends AbstractEntity {
    @Column(nullable = false)
    private int currentRating;
    @Column(nullable = false)
    private UUID userId;

    @Builder
    public Rating(UUID id, Long version, Instant createdAt, Instant updatedAt, int currentRating, UUID userId) {
        super(id, version, createdAt, updatedAt);
        this.currentRating = currentRating;
        this.userId = userId;
    }
}
