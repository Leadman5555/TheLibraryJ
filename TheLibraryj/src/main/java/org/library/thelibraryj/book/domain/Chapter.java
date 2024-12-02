package org.library.thelibraryj.book.domain;

import jakarta.persistence.Column;
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

import java.time.Instant;
import java.util.UUID;

@Data
@Entity(name = "chapter")
@Table(name = "library_chapters")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
class Chapter extends AbstractEntity {
    @Column(columnDefinition = "TEXT")
    private String text;

    @MapsId
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_preview_id")
    private ChapterPreview chapterPreview;

    @Builder
    public Chapter(UUID id, Long version, Instant createdAt, Instant updatedAt, String text) {
        super(id, version, createdAt, updatedAt);
        this.text = text;
    }
}
