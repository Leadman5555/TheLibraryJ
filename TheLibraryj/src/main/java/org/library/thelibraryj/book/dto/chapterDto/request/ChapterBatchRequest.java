package org.library.thelibraryj.book.dto.chapterDto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record ChapterBatchRequest(List<MultipartFile> chapterFiles, UUID bookId, String authorEmail) {
}
