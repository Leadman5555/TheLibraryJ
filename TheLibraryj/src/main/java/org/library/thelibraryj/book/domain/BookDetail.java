package org.library.thelibraryj.book.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Setter
@Getter
@Table(name = "library_book_details")
class BookDetail extends AbstractEntity {
    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private UUID authorId;
    @Column
    @Size(max = 2000)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "book_detail_id", referencedColumnName = "id")
    private List<ChapterPreview> chapterPreviews;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "book_detail_id", referencedColumnName = "id")
    private List<Rating> ratings;

    @Builder
    public BookDetail(UUID id, Long version, Instant createdAt, Instant updatedAt, String author, UUID authorId, String description, List<ChapterPreview> chapters, List<Rating> ratings) {
        super(id, version, createdAt, updatedAt);
        this.author = author;
        this.authorId = authorId;
        this.description = description;
        this.chapterPreviews = chapters;
        this.ratings = ratings;
    }
}
