package org.library.thelibraryj.book.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "library_book_details")
class BookDetail extends AbstractEntity {
    private String author;
    private UUID bookPreviewId;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "book_detail_id", referencedColumnName = "id")
    private List<ChapterPreview> chapters;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "book_detail_id", referencedColumnName = "id")
    private List<Rating> ratings;
    private String description;

    @Builder
    public BookDetail(UUID id, Long version, Instant createdAt, Instant updatedAt, String author, UUID bookPreviewId, List<ChapterPreview> chapters, List<Rating> ratings, String description) {
        super(id, version, createdAt, updatedAt);
        this.author = author;
        this.bookPreviewId = bookPreviewId;
        this.chapters = chapters;
        this.ratings = ratings;
        this.description = description;
    }
}
