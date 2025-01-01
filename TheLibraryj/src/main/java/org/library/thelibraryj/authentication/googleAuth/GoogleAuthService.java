package org.library.thelibraryj.authentication.googleAuth;

import org.library.thelibraryj.authentication.googleAuth.dto.GoogleCallbackResponse;
import org.library.thelibraryj.authentication.googleAuth.dto.GoogleLinkResponse;

public interface GoogleAuthService {
    GoogleLinkResponse getGoogleAuthUrl();
    GoogleCallbackResponse getGoogleAuthToken(String code);
}
