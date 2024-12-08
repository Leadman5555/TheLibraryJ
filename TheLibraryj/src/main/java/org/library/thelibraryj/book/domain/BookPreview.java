package org.library.thelibraryj.book.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "bookPreview")
@NoArgsConstructor
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

    @ElementCollection(targetClass = BookTag.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "book_tag", joinColumns =  @JoinColumn(name = "book_preview_id"))
    @Column(name = "tag")
    private List<BookTag> bookTags;

    @MapsId
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_detail_id")
    private BookDetail bookDetail;

    @Builder
    public BookPreview(UUID id, Long version, Instant createdAt, Instant updatedAt, String title, int chapterCount, float averageRating, int ratingCount, BookState bookState,
                       List<BookTag> bookTags) {
        super(id, version, createdAt, updatedAt);
        this.title = title;
        this.chapterCount = chapterCount;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.bookState = bookState;
        this.bookTags = bookTags;
    }
    @Transient
    @JsonIgnore
    public void increaseChapterCount(int increment) {
        this.chapterCount+=increment;
    }
}
