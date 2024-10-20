package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface ChapterPreviewRepository extends BaseJpaRepository<ChapterPreview, UUID> {
    @Query("""
    select c from  chapterPreview c
    where c.bookDetail.id = :id
    """)
    List<ChapterPreview> getAllChapterPreviewsForBook(@Param("id") UUID id);
}
