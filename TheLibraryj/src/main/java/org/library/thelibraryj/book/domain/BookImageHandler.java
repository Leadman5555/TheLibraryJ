package org.library.thelibraryj.book.domain;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
class BookImageHandler {
    @Getter
    private final byte[] defaultImage;
    private final Path basePath;

    public BookImageHandler(@Value("${library.book.image_source}") String imageSourcePath) {
        try {
            defaultImage = Files.readAllBytes(Path.of(imageSourcePath, "default.jpg"));
        } catch (IOException e) {
            throw new RuntimeException("Could not load default image for BookImageHandler");
        }
        basePath = Path.of(imageSourcePath);
    }

    public byte[] fetchCoverImage(String forTitle) {
        try {
            return Files.readAllBytes(basePath.resolve(forTitle + ".jpg"));
        } catch (IOException e) {
            return defaultImage;
        }
    }

    public byte[] upsertCoverImage(String forTitle, MultipartFile image) {
        try {
            Files.copy(image.getInputStream(), basePath.resolve(forTitle + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
            return image.getBytes();
        } catch (IOException e) {
            return defaultImage;
        }
    }

    public boolean removeExistingCoverImage(String forTitle) {
        try {
            Files.deleteIfExists(basePath.resolve(forTitle + ".jpg"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
