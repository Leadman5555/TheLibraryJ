package org.library.thelibraryj.infrastructure.textParsers.fileParsers.domain;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

class TxtParser {
    @Nullable
    public static String parseTxtTextFile(MultipartFile textFile) {
        try {
            return new String(textFile.getBytes());
        } catch (IOException _) {
           return null;
        }
    }
}
