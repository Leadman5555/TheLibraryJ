package org.library.thelibraryj.infrastructure.textParsers.fileParsers.domain;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

class OdtParser {
    private final Tika tika = new Tika();

    @Nullable
    public String parseOdtTextFile(MultipartFile textFile) {
        try (InputStream inputStream = textFile.getInputStream()) {
            return tika.parseToString(inputStream);
        } catch (IOException | TikaException _) {
            return null;
        }
    }
}
