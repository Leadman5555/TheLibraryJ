package org.library.thelibraryj.authentication.userAuth.dto.request;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record UserCreationRequest(String email, char[] password, String username, @Nullable MultipartFile profileImage) {
}
