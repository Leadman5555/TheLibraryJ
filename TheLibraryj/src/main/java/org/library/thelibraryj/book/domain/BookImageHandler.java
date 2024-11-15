package org.library.thelibraryj.book.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class BookImageHandler {
    private byte[] defaultImage;
    private final Path basePath;

    public BookImageHandler(@Value("${library.book.image_source}") String imageSourcePath) {
        try {
            defaultImage = Files.readAllBytes(Path.of(imageSourcePath, "default.jpg"));
        } catch (IOException e) {
            defaultImage = null;
        }
        basePath = Path.of(imageSourcePath);
    }

    public byte[] fetchCoverImage(String forTitle) {
        try {
            return Files.readAllBytes(basePath.resolve(forTitle));
        } catch (IOException e) {
            return defaultImage;
        }
    }

    public void upsertCoverImage(String forTitle, MultipartFile image) {
        try {
            Files.copy(image.getInputStream(), basePath.resolve(forTitle + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException _) {
        }
    }
}
