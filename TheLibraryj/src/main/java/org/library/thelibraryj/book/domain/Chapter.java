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
import lombok.Setter;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity(name = "chapter")
@Table(name = "library_chapters")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Chapter extends AbstractEntity {
    @Column
    private String text;

    @Setter
    @MapsId
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "chapterPreview_id")
    private ChapterPreview chapterPreview;

    @Builder
    public Chapter(UUID id, Long version, Instant createdAt, Instant updatedAt, String text) {
        super(id, version, createdAt, updatedAt);
        this.text = text;
    }
}
