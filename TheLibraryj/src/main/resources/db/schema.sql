DROP SCHEMA IF EXISTS library CASCADE;
CREATE SCHEMA library;
DROP TABLE IF EXISTS library.library_book_details;
CREATE TABLE library.library_book_details
(
    description VARCHAR(800),
    author      VARCHAR(25) NOT NULL,
    author_id   UUID         NOT NULL,
    id          UUID         NOT NULL,
    version     BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT pk_library_book_details PRIMARY KEY (id)
);
DROP TABLE IF EXISTS library.library_book_previews;
CREATE TABLE library.library_book_previews
(
    title          VARCHAR(252) UNIQUE NOT NULL,
    book_detail_id UUID                NOT NULL,
    version        BIGINT              NOT NULL DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    chapter_count  INT                 NOT NULL,
    rating_count   INT                 NOT NULL,
    average_rating FLOAT               NOT NULL,
    book_state     SMALLINT            NOT NULL,
    CONSTRAINT pk_library_book_previews PRIMARY KEY (book_detail_id)
);
DROP TABLE IF EXISTS library.book_tag;
CREATE TABLE library.book_tag
(
    book_preview_id UUID NOT NULL,
    tag             SMALLINT
);
DROP TABLE IF EXISTS library.library_chapter_previews;
CREATE TABLE library.library_chapter_previews
(
    title          VARCHAR(252),
    id             UUID   NOT NULL,
    book_detail_id UUID   NOT NULL,
    version        BIGINT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    number         INT    NOT NULL,
    CONSTRAINT pk_library_chapter_previews PRIMARY KEY (id)
);
DROP TABLE IF EXISTS library.library_chapters;
CREATE TABLE library.library_chapters
(
    text               VARCHAR(18050),
    chapter_preview_id UUID   NOT NULL,
    version            BIGINT NOT NULL DEFAULT 0,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    CONSTRAINT pk_library_chapters PRIMARY KEY (chapter_preview_id)
);
DROP TABLE IF EXISTS library.library_ratings;
CREATE TABLE library.library_ratings
(
    comment        VARCHAR(252),
    username        VARCHAR(25) UNIQUE NOT NULL,
    id             UUID   NOT NULL,
    user_id        UUID   NOT NULL,
    book_detail_id UUID   NOT NULL,
    version        BIGINT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    current_rating INT    NOT NULL,
    CONSTRAINT pk_library_ratings PRIMARY KEY (id)
);
DROP TABLE IF EXISTS library.library_user_info;
CREATE TABLE library.library_user_info
(
    username        VARCHAR(25) UNIQUE NOT NULL,
    email           VARCHAR(48) UNIQUE NOT NULL,
    id              UUID               NOT NULL,
    user_auth_id    UUID               NOT NULL,
    version         BIGINT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP,
    data_updated_at TIMESTAMP,
    rank            INTEGER            NOT NULL DEFAULT 0,
    current_score   INTEGER            NOT NULL DEFAULT 0,
    CONSTRAINT pk_library_user_info PRIMARY KEY (id)
);
DROP TABLE IF EXISTS library.library_user_auth;
CREATE TABLE library.library_user_auth
(
    password   VARCHAR(200),
    email      VARCHAR(48) UNIQUE NOT NULL,
    id         UUID               NOT NULL,
    version    BIGINT             NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    role       VARCHAR(8)         NOT NULL,
    is_enabled BOOLEAN            NOT NULL DEFAULT false,
    is_google  BOOLEAN            NOT NULL DEFAULT false,
    CONSTRAINT pk_library_user_auth PRIMARY KEY (id)
);
DROP TABLE IF EXISTS library.library_tokens;
CREATE TABLE library.library_tokens
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
    ADD CONSTRAINT fk_library_bookpreviews_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library.library_book_details (id);
ALTER TABLE library.library_chapter_previews
    ADD CONSTRAINT fk_library_chapterpreviews_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library.library_book_details (id);
ALTER TABLE library.library_chapters
    ADD CONSTRAINT fk_library_chapters_on_chapterpreview FOREIGN KEY (chapter_preview_id) REFERENCES library.library_chapter_previews (id);
ALTER TABLE library.library_ratings
    ADD CONSTRAINT fk_library_ratings_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library.library_book_details (id);
ALTER TABLE library.book_tag
    ADD CONSTRAINT fk_book_tag_on_book_preview FOREIGN KEY (book_preview_id) REFERENCES library.library_book_previews (book_detail_id);