package org.library.thelibraryj.book.dto.chapterDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record ChapterRequest(MultipartFile chapterFile, UUID bookId, String authorEmail) { }
