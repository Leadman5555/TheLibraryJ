package org.library.thelibraryj.infrastructure.textParsers.fileParsers.domain;

import org.library.thelibraryj.infrastructure.exception.ChapterTextParsingException;
import org.library.thelibraryj.infrastructure.textParsers.fileParsers.TextParser;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * This component expects to receive files that have already been verified to be of allowed format.
 * As such, it checks the extension solely to choose the correct parser - it does not verify the file type or content.
 * **/
@Component
class TextParserImpl implements TextParser {

    private final OdtParser odtParser = new OdtParser();

    @Override
    @Nullable
    public String parseTextFile(MultipartFile textFile) throws ChapterTextParsingException {
        String filename = textFile.getOriginalFilename();
        if (filename == null) throw new ChapterTextParsingException("Missing file name.");
        String extension = filename.substring(textFile.getOriginalFilename().lastIndexOf('.') + 1);
        return switch (extension) {
            case "txt" -> TxtParser.parseTxtTextFile(textFile);
            case "odt" -> normalizeQuotes(odtParser.parseOdtTextFile(textFile));
            case "doc" -> normalizeQuotes(DocParser.parseDocTextFile(textFile));
            case "docx" -> normalizeQuotes(DocxParser.parseDocxTextFile(textFile));
            default -> throw new ChapterTextParsingException("Unsupported file format.");
        };
    }

    @Nullable
    private static String normalizeQuotes(String text) {
        if(text == null) return null;
        return text.replace("“", "\"")
                .replace("”", "\"")
                .replace("‘", "'")
                .replace("’", "'");

    }
}
