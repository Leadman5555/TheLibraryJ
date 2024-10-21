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

@Data
@Entity(name = "chapterPreview")
@Table(name = "library_chapterPreviews")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
class ChapterPreview extends AbstractEntity {
    @Column
    private String title;
    @Column(nullable = false)
    private int number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bookDetail_id")
    private BookDetail bookDetail;

    @Builder
    public ChapterPreview(UUID id, Long version, Instant createdAt, Instant updatedAt, String title, int number, BookDetail bookDetail) {
        super(id, version, createdAt, updatedAt);
        this.title = title;
        this.number = number;
        this.bookDetail = bookDetail;
    }
}
