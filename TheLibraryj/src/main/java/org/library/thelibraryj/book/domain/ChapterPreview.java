package org.library.thelibraryj.book.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.ConstraintMode.CONSTRAINT;

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

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bookDetail_id", foreignKey = @ForeignKey(value = CONSTRAINT, foreignKeyDefinition = "FOREIGN KEY (bookDetail_id) REFERENCES library_bookDetails(id) ON DELETE CASCADE"))
    private BookDetail bookDetail;

    @Builder
    public ChapterPreview(UUID id, Long version, Instant createdAt, Instant updatedAt, String title, int number) {
        super(id, version, createdAt, updatedAt);
        this.title = title;
        this.number = number;
    }
}
