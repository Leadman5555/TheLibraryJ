package org.library.thelibraryj.book.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface ChapterPreviewRepository extends BaseJpaRepository<ChapterPreview, UUID> {
}
