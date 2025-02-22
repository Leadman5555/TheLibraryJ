package org.library.thelibraryj.infrastructure.textParsers.fileParsers.domain;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

class DocParser {

    @Nullable
    public static String parseDocTextFile(MultipartFile textFile) {
        try (HWPFDocument document = new HWPFDocument(textFile.getInputStream())) {
            Range range = document.getRange();
            StringBuilder stringBuilder = new StringBuilder();
            int paragraphCount = range.numParagraphs()-1;
            for (int i = 0; i < paragraphCount; i++) {
                stringBuilder.append(range.getParagraph(i).text());
                stringBuilder.append('\n');
            }
            return stringBuilder.append(range.getParagraph(paragraphCount).text()).toString();
        } catch (IOException | IllegalArgumentException _) {
            return null;
        }
    }
}
