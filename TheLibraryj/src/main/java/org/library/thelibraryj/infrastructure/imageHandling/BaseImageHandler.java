package org.library.thelibraryj.infrastructure.imageHandling;

import lombok.Getter;
import org.library.thelibraryj.infrastructure.exception.FileSystemInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class BaseImageHandler {
    @Getter
    protected final String defaultImageUrl;
    protected final byte[] defaultImage;
    protected final Path basePath;
    private final String subHandlerBase;

    @Value("${library.image.base}")
    private String libraryBasePath;

    protected BaseImageHandler(String defaultImageName, String subHandlerBase) throws FileSystemInitializationException {
        this.subHandlerBase = subHandlerBase;
        Path libraryBase = Path.of(libraryBasePath);
        Path base = libraryBase.resolve(subHandlerBase);
        Path defaultImagePath = base.resolve(defaultImageName);
        if (!Files.isDirectory(base)) throw new FileSystemInitializationException("Base path is not a directory");
        if (!Files.exists(defaultImagePath))
            throw new FileSystemInitializationException("Default image does not exist");
        try {
            defaultImage = Files.readAllBytes(defaultImagePath);
        } catch (IOException e) {
            throw new FileSystemInitializationException("Default image cannot be read");
        }
        this.basePath = base;
        this.defaultImageUrl = formatImageUrl(defaultImageName);
    }

    public String fetchImageUrl(String identification) {
        final Path resolvedPath = basePath.resolve(identification + ".jpg");
        if (Files.exists(resolvedPath))
            return formatImageUrl(identification);
        return defaultImageUrl;

    }


    public byte[] fetchImageAsBytes(String identification) {
        try {
            return Files.readAllBytes(basePath.resolve(identification + ".jpg"));
        } catch (IOException e) {
            return defaultImage;
        }
    }

    public String upsertImage(String identification, MultipartFile image, boolean returnDefaultOnFail) {
        try {
            final Path resolvedPath = basePath.resolve(identification + ".jpg");
            Files.copy(image.getInputStream(), resolvedPath, StandardCopyOption.REPLACE_EXISTING);
            return formatImageUrl(identification);
        } catch (IOException e) {
            return returnDefaultOnFail ? defaultImageUrl : null;
        }
    }

    public boolean removeExistingImage(String identification) {
        try {
            Files.deleteIfExists(basePath.resolve(identification + ".jpg"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String formatImageUrl(String imageId){
        //server-part/image-controller-endpoint/subHandlerBase/imageId
    }
}
