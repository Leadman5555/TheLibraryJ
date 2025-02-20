package org.library.thelibraryj.infrastructure.validators.fileValidators;

import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public enum FileFormatMatcher {
    TEXT_FILE_FORMAT_MATCHER(
            List.of(
                    MediaType.TEXT_PLAIN_VALUE,
                    "application/vnd.oasis.opendocument.text",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ),
            List.of("txt", "odt", "doc", "docx"),
            List.of(
                    MediaType.TEXT_PLAIN_VALUE,
                    "application/vnd.oasis.opendocument.text",
                    "application/x-tika-msoffice",
                    "application/x-tika-ooxml"
            )
    ),
    IMAGE_FILE_FORMAT_MATCHER(
            List.of(
                    "image/png",
                    "image/jpeg",
                    "image/webp"
            ),
            List.of("png", "jpg", "jpeg", "webp"),
            List.of(
                    "image/png",
                    "image/jpeg",
                    "image/webp"
            )
    );

    private final List<String> ALLOWED_MIME_TYPES;

    private final List<String> ALLOWED_EXTENSIONS;

    private final List<String> ALLOWED_TIKA_MIME_TYPES;

    private final Tika tika;

    FileFormatMatcher(List<String> ALLOWED_MIME_TYPES, List<String> ALLOWED_EXTENSIONS, List<String> ALLOWED_TIKA_MIME_TYPES) {
        this.ALLOWED_MIME_TYPES = ALLOWED_MIME_TYPES;
        this.ALLOWED_EXTENSIONS = ALLOWED_EXTENSIONS;
        this.ALLOWED_TIKA_MIME_TYPES = ALLOWED_TIKA_MIME_TYPES;
        this.tika = new Tika();
    }

    private boolean isValidFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) return false;
        int lastIndex = filename.lastIndexOf('.');
        if(lastIndex < 1) return false;
        return ALLOWED_EXTENSIONS.contains(filename.substring(lastIndex + 1).toLowerCase());
    }

    private boolean isValidMimeType(MultipartFile file) {
        return ALLOWED_MIME_TYPES.contains(file.getContentType());
    }

    private boolean isValidByTika(MultipartFile file, Tika tika) {
        try {
            return ALLOWED_TIKA_MIME_TYPES.contains(tika.detect(file.getInputStream()));
        } catch (IOException _) {
            return false;
        }
    }

    public boolean isValidFormat(MultipartFile file) {
        return isValidFileExtension(file) && isValidMimeType(file) && isValidByTika(file, tika);
    }

    public boolean isValidFormat(List<MultipartFile> fileList) {
        return fileList.stream().allMatch(file -> isValidFileExtension(file) && isValidMimeType(file) && isValidByTika(file, tika));
    }
}
