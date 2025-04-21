package org.library.thelibraryj.infrastructure.imageHandling;

import lombok.Getter;
import org.library.thelibraryj.infrastructure.exception.FileSystemInitializationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class BaseImageHandler {

    private static final Map<String, BaseImageHandler> registeredImageHandlers = new HashMap<>();

    @Getter
    protected final String defaultImageUrl;
    protected final byte[] defaultImage;
    protected final Path basePath;

    private final String imageResourcePath;


    private static void registerImageHandler(String subHandlerBase, BaseImageHandler imageHandler) throws FileSystemInitializationException {
        if (registeredImageHandlers.containsKey(subHandlerBase))
            throw new FileSystemInitializationException("Handler already registered");
        registeredImageHandlers.put(subHandlerBase, imageHandler);
    }

    public static Optional<BaseImageHandler> getHandler(String subHandlerBase) {
        BaseImageHandler handler = registeredImageHandlers.get(subHandlerBase);
        return handler == null ? Optional.empty() : Optional.of(handler);
    }

    protected BaseImageHandler(String defaultImageName, String subHandlerBase, ImageHandlerProperties properties) throws FileSystemInitializationException {
        if (subHandlerBase.charAt(0) == '/') subHandlerBase = subHandlerBase.substring(1);
        if (subHandlerBase.charAt(subHandlerBase.length() - 1) == '/')
            subHandlerBase = subHandlerBase.substring(0, subHandlerBase.length() - 1);

        this.imageResourcePath = properties.getEndpoint_domain() + properties.getMapping() + '/' + subHandlerBase + '/';
        Path libraryBase = Path.of(properties.getBase());
        Path base = libraryBase.resolve(subHandlerBase);
        Path defaultImagePath = base.resolve(defaultImageName);
        if (!Files.exists(base) || !Files.isDirectory(base))
            throw new FileSystemInitializationException("Base path is not a directory");
        if (!Files.exists(defaultImagePath))
            throw new FileSystemInitializationException("Default image does not exist");
        try {
            defaultImage = Files.readAllBytes(defaultImagePath);
        } catch (IOException e) {
            throw new FileSystemInitializationException("Default image cannot be read");
        }
        this.basePath = base;
        this.defaultImageUrl = formatImageUrl(defaultImageName);
        registerImageHandler(subHandlerBase, this);
    }

    public String fetchImageUrl(String identification) {
        final Path resolvedPath = basePath.resolve(identification + ".jpg");
        if (Files.exists(resolvedPath))
            return formatImageUrl(identification);
        return defaultImageUrl;

    }


    public byte[] fetchImageOrDefaultAsBytes(String identification) {
        try {
            return Files.readAllBytes(basePath.resolve(identification + ".jpg"));
        } catch (IOException e) {
            return defaultImage;
        }
    }

    public Optional<byte[]> fetchImageAsBytes(String identification) {
        try {
            return Optional.of(Files.readAllBytes(basePath.resolve(identification + ".jpg")));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public String upsertAndFetchImage(String identification, MultipartFile image) {
        try {
            final Path resolvedPath = basePath.resolve(identification + ".jpg");
            Files.copy(image.getInputStream(), resolvedPath, StandardCopyOption.REPLACE_EXISTING);
            return formatImageUrl(identification);
        } catch (IOException e) {
            return defaultImageUrl;
        }
    }

    public Optional<String> upsertImage(String identification, MultipartFile image) {
        try {
            final Path resolvedPath = basePath.resolve(identification + ".jpg");
            Files.copy(image.getInputStream(), resolvedPath, StandardCopyOption.REPLACE_EXISTING);
            return Optional.of(formatImageUrl(identification));
        } catch (IOException e) {
            return Optional.empty();
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

    private String formatImageUrl(String imageId) {
        return this.imageResourcePath + imageId;
    }
}
