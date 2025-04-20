package org.library.thelibraryj.userInfo.domain;

import org.library.thelibraryj.infrastructure.imageHandling.BaseImageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class UserInfoImageHandler extends BaseImageHandler {

    public UserInfoImageHandler(@Value("${library.user.image_source}") String imageSourcePath) {
        super("default.jpg", imageSourcePath);
    }

}
