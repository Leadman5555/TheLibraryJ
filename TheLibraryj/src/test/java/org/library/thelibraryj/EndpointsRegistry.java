package org.library.thelibraryj;

public class EndpointsRegistry {

    public static final String BASE_URL = "";
    public static final String BASE_AUTH_FREE_URL = BASE_URL + "/na";

    public static final String PUBLIC_AUTH_URL = BASE_AUTH_FREE_URL + "/auth";
    public static final String PUBLIC_AUTH_PASSWORD_URL = PUBLIC_AUTH_URL + "/password";
    public static final String PUBLIC_AUTH_ACTIVATION_URL = PUBLIC_AUTH_URL + "/activation";
    public static final String PUBLIC_AUTH_LOGIN_URL = PUBLIC_AUTH_URL + "/login";
    public static final String PUBLIC_AUTH_REFRESH_TOKEN_URL = PUBLIC_AUTH_URL + "/refresh";
    public static final String PUBLIC_AUTH_REGISTER_URL = PUBLIC_AUTH_URL + "/register";

    public static final String PUBLIC_BOOKS_URL = BASE_AUTH_FREE_URL + "/books";
    public static final String PRIVATE_BOOKS_URL = BASE_URL + "/books";
    public static final String PRIVATE_BOOKS_BOOK_URL = PRIVATE_BOOKS_URL + "/book";
    public static final String PUBLIC_BOOKS_PREVIEW_URL = PUBLIC_BOOKS_URL + "/preview";
    public static final String PUBLIC_BOOKS_FILTER_URL = PUBLIC_BOOKS_URL + "/filtered";
    public static final String PRIVATE_BOOKS_RATING_URL = PRIVATE_BOOKS_URL + "/rating";
    public static final String PRIVATE_BOOKS_CHAPTER_URL = PRIVATE_BOOKS_URL + "/chapter";

    public static final String PUBLIC_USER_URL = BASE_AUTH_FREE_URL + "/user";
    public static final String PRIVATE_USER_URL = BASE_URL + "/user";
    public static final String PRIVATE_USER_PROFILE_URL = PRIVATE_USER_URL + "/profile";
    public static final String PRIVATE_USER_BOOK_URL = PRIVATE_USER_URL + "/book";
    public static final String PRIVATE_USER_BOOK_TOKEN_URL = PRIVATE_USER_BOOK_URL + "/token";

    public static final String PRIVATE_EMAIL_URL = BASE_URL + "/email";

    public static final String PUBLIC_IMAGE_URL = BASE_AUTH_FREE_URL + "/image";
}
