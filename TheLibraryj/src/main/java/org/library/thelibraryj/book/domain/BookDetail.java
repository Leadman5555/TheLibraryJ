package org.library.thelibraryj.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity(name = "bookDetail")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Setter
@Table(name = "library_book_details")
class BookDetail extends AbstractEntity {
    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private UUID authorId;
    @Column
    @Size(max = 2000)
    private String description;

    @Builder
    public BookDetail(UUID id, Long version, Instant createdAt, Instant updatedAt, String author, UUID authorId, String description) {
        super(id, version, createdAt, updatedAt);
        this.author = author;
        this.authorId = authorId;
        this.description = description;
    }

//    public void addChapter(ChapterPreview chapterPreview) {
//        chapterPreviews.add(chapterPreview);
//        chapterPreview.setBookDetail(this);
//    }
//
//    public void removeChapter(ChapterPreview chapterPreview) {
//        chapterPreviews.remove(chapterPreview);
//        chapterPreview.setBookDetail(null);
//    }
//
//    public void addRating(Rating rating) {
//        ratings.add(rating);
//        rating.setBookDetail(this);
//    }
//
//    public void removeRating(Rating rating) {
//        ratings.remove(rating);
//        rating.setBookDetail(null);
//    }
}
