package org.library.thelibraryj.infrastructure.textParsers.fileParsers.domain;

import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

class DocxParser {

    @Nullable
    public static String parseDocxTextFile(MultipartFile textFile) {
        try (XWPFDocument document = new XWPFDocument(textFile.getInputStream())) {
            StringBuilder stringBuilder = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                stringBuilder.append(paragraph.getText());
                stringBuilder.append('\n');
            }
            return stringBuilder.toString();
        } catch (IOException | UnsupportedFileFormatException _) {
            return null;
        }
    }
}
