package org.library.thelibraryj.infrastructure.textParsers.fileParsers;

import org.library.thelibraryj.infrastructure.exception.ChapterTextParsingException;
import org.springframework.web.multipart.MultipartFile;

public interface TextParser {
    String parseTextFile(MultipartFile textFile) throws ChapterTextParsingException;
}
