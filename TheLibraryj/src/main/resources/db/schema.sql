CREATE SCHEMA IF NOT EXISTS library;
CREATE TABLE IF NOT EXISTS library.library_book_details
(
    description VARCHAR(1000),
    author      VARCHAR(25) NOT NULL,
    author_id   UUID        NOT NULL,
    id          UUID        NOT NULL,
    version     BIGINT      NOT NULL DEFAULT 0,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT pk_library_book_details PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS library.library_book_previews
(
    title          VARCHAR(50) UNIQUE NOT NULL,
    book_detail_id UUID               NOT NULL,
    version        BIGINT             NOT NULL DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    chapter_count  INT                NOT NULL,
    rating_count   INT                NOT NULL,
    average_rating FLOAT              NOT NULL,
    book_state     SMALLINT           NOT NULL,
    CONSTRAINT pk_library_book_previews PRIMARY KEY (book_detail_id)
);
CREATE TABLE IF NOT EXISTS library.book_tag
(
    book_preview_id UUID NOT NULL,
    tag             SMALLINT
);
CREATE TABLE IF NOT EXISTS library.library_chapter_previews
(
    title          VARCHAR(50),
    id             UUID   NOT NULL,
    book_detail_id UUID   NOT NULL,
    version        BIGINT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    number         INT    NOT NULL,
    CONSTRAINT pk_library_chapter_previews PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS library.library_chapters
(
    text               VARCHAR(18050),
    chapter_preview_id UUID   NOT NULL,
    version            BIGINT NOT NULL DEFAULT 0,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    CONSTRAINT pk_library_chapters PRIMARY KEY (chapter_preview_id)
);
CREATE TABLE IF NOT EXISTS library.library_ratings
(
    comment        VARCHAR(252),
    username       VARCHAR(32) NOT NULL,
    id             UUID        NOT NULL,
    user_id        UUID        NOT NULL,
    book_detail_id UUID        NOT NULL,
    version        BIGINT      NOT NULL DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    current_rating INT         NOT NULL,
    CONSTRAINT pk_library_ratings PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS library.library_user_info
(
    status          VARCHAR(350),
    username        VARCHAR(32) UNIQUE NOT NULL,
    email           VARCHAR(48) UNIQUE NOT NULL,
    id              UUID               NOT NULL,
    user_auth_id    UUID               NOT NULL,
    version         BIGINT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    data_updated_at TIMESTAMP,
    rank            INTEGER            NOT NULL DEFAULT 0,
    current_score   INTEGER            NOT NULL DEFAULT 0,
    preference      SMALLINT           NOT NULL DEFAULT 0,
    CONSTRAINT pk_library_user_info PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS library.library_user_auth
(
    password   VARCHAR(200),
    email      VARCHAR(48) UNIQUE NOT NULL,
    id         UUID               NOT NULL,
    version    BIGINT             NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    role       VARCHAR(16)        NOT NULL,
    is_enabled BOOLEAN            NOT NULL DEFAULT false,
    is_google  BOOLEAN            NOT NULL DEFAULT false,
    CONSTRAINT pk_library_user_auth PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS library.library_tokens
(
    id          UUID      NOT NULL,
    token       UUID      NOT NULL,
    for_user_id UUID      NOT NULL,
    version     BIGINT    NOT NULL DEFAULT 0,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    expires_at  TIMESTAMP NOT NULL,
    is_used     BOOLEAN   NOT NULL DEFAULT false,
    CONSTRAINT pk_library_tokens PRIMARY KEY (id)
);

ALTER TABLE library.library_book_previews
    ADD CONSTRAINT fk_library_bookpreviews_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library.library_book_details (id) ON DELETE CASCADE;
ALTER TABLE library.library_chapter_previews
    ADD CONSTRAINT fk_library_chapterpreviews_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library.library_book_details (id);
ALTER TABLE library.library_chapters
    ADD CONSTRAINT fk_library_chapters_on_chapterpreview FOREIGN KEY (chapter_preview_id) REFERENCES library.library_chapter_previews (id) ON DELETE CASCADE;
ALTER TABLE library.library_ratings
    ADD CONSTRAINT fk_library_ratings_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library.library_book_details (id);
ALTER TABLE library.book_tag
    ADD CONSTRAINT fk_book_tag_on_book_preview FOREIGN KEY (book_preview_id) REFERENCES library.library_book_previews (book_detail_id) ON DELETE CASCADE;