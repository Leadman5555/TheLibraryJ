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

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@Table(name = "library_book_previews")
class BookPreview extends AbstractEntity {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private int chapterCount;
    @Column(nullable = false)
    private float averageRating;
    @Column(nullable = false)
    private  int ratingCount;
    @Column(nullable = false)
    private UUID bookDetailsId;

    @Builder
    public BookPreview(UUID id, Long version, Instant createdAt, Instant updatedAt, String title, int chapterCount, float averageRating, int ratingCount, UUID bookDetailsId) {
        super(id, version, createdAt, updatedAt);
        this.title = title;
        this.chapterCount = chapterCount;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.bookDetailsId = bookDetailsId;
    }
}
