package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
interface ChapterRepository extends BaseJpaRepository<Chapter, UUID> {
    @Query("""
                select text from chapter
                where id = :id
            """)
    Optional<String> getChapterContentById(@Param("id") UUID id);

    @Query("""
                select chapter from chapter
                where id in :previewIds
                order by id asc
            """)
    List<Chapter> fetchChaptersByPreviewIds(@Param("previewIds") Set<UUID> previewIds);


    @Modifying
    @Query("""
    delete from chapter
    where id in (
    select id from chapterPreview
    where bookDetail.id = :bookId
    )
    """)
    void deleteBook(@Param("bookId") UUID bookId);
}