package org.library.thelibraryj.userInfo.domain;

import org.library.thelibraryj.infrastructure.imageHandling.BaseImageHandler;
import org.library.thelibraryj.infrastructure.imageHandling.ImageHandlerProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class UserInfoImageHandler extends BaseImageHandler {

    public UserInfoImageHandler(@Value("${library.user.image_source}") String imageSourcePath, ImageHandlerProperties properties) {
        super("default.jpg", imageSourcePath, properties);
    }

}
