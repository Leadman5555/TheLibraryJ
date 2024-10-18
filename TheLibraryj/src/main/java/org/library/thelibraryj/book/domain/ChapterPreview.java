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

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "library_chapter_previews")
@NoArgsConstructor
class ChapterPreview extends AbstractEntity {
    @Column
    private String title;
    @Column(nullable = false)
    private int number;

    @Builder
    public ChapterPreview(UUID id, Long version, Instant createdAt, Instant updatedAt, String title, int number) {
        super(id, version, createdAt, updatedAt);
        this.title = title;
        this.number = number;
    }
}
