package org.library.thelibraryj.book.domain;

import org.library.thelibraryj.infrastructure.imageHandling.BaseImageHandler;
import org.library.thelibraryj.infrastructure.imageHandling.ImageHandlerProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class BookImageHandler extends BaseImageHandler {
    public BookImageHandler(@Value("${library.book.image_source}") String imageSourcePath, ImageHandlerProperties properties) {
        super("default.jpg", imageSourcePath, properties);
    }
}
