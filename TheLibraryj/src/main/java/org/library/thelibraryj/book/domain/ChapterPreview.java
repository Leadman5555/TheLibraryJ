package org.library.thelibraryj.book.domain;

import jakarta.persistence.*;
import lombok.*;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.ConstraintMode.CONSTRAINT;

@Data
@Entity(name = "chapter_preview")
@Table(name = "library_chapter_previews")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
class ChapterPreview extends AbstractEntity {
    @Column
    private String title;
    @Column(nullable = false)
    private int number;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bookDetail_id", foreignKey = @ForeignKey(value = CONSTRAINT, foreignKeyDefinition = "FOREIGN KEY (bookDetail_id) REFERENCES bookDetail(id) ON DELETE CASCADE"))
    private BookDetail bookDetail;

    @Builder
    public ChapterPreview(UUID id, Long version, Instant createdAt, Instant updatedAt, String title, int number) {
        super(id, version, createdAt, updatedAt);
        this.title = title;
        this.number = number;
    }
}
