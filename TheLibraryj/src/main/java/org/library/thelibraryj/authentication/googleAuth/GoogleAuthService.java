package org.library.thelibraryj.authentication.googleAuth;

import org.library.thelibraryj.authentication.googleAuth.dto.GoogleCallbackResponseWrapper;
import org.library.thelibraryj.authentication.googleAuth.dto.GoogleLinkResponse;

public interface GoogleAuthService {
    GoogleLinkResponse getGoogleAuthUrl();
    GoogleCallbackResponseWrapper getGoogleAuthToken(String code);
}
