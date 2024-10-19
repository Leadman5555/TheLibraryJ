package org.library.thelibraryj.book.domain;

import jakarta.persistence.*;
import lombok.*;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "library_book_previews")
class BookPreview extends AbstractEntity {
    @Column(nullable = false, unique = true)
    private String title;
    @Column(nullable = false)
    private int chapterCount;
    @Column(nullable = false)
    private float averageRating;
    @Column(nullable = false)
    private  int ratingCount;
    @Column(nullable = false)
    private BookState bookState;

    @MapsId
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private BookDetail bookDetail;

    @Builder
    public BookPreview(UUID id, Long version, Instant createdAt, Instant updatedAt, String title, int chapterCount, float averageRating, int ratingCount, BookState bookState, BookDetail bookDetail) {
        super(id, version, createdAt, updatedAt);
        this.title = title;
        this.chapterCount = chapterCount;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.bookState = bookState;
        this.bookDetail = bookDetail;
    }
}
