package org.library.thelibraryj.userInfo.domain;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
class UserInfoImageHandler {
    @Getter
    private final byte[] defaultImage;
    private final Path basePath;

    public UserInfoImageHandler(@Value("${library.user.image_source}") String imageSourcePath) {
        try {
            defaultImage = Files.readAllBytes(Path.of(imageSourcePath, "default.jpg"));
        } catch (IOException e) {
            throw new RuntimeException("Could not load default image for UserInfoImageHandler");
        }
        basePath = Path.of(imageSourcePath);
    }

    public byte[] fetchProfileImage(UUID forUUID) {
        try {
            return Files.readAllBytes(basePath.resolve(forUUID + ".jpg"));
        } catch (IOException e) {
            return defaultImage;
        }
    }

    public boolean upsertProfileImageImage(UUID forUUID, MultipartFile image) {
        try {
            Files.copy(image.getInputStream(), basePath.resolve(forUUID + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean removeExistingProfileImage(UUID forUUID) {
        try {
            Files.deleteIfExists(basePath.resolve(forUUID + ".jpg"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
