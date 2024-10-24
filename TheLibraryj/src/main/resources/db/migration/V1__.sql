DROP SCHEMA IF EXISTS library CASCADE;
CREATE SCHEMA library;
DROP TABLE IF EXISTS library.library_book_details;
CREATE TABLE library.library_book_details
(
    id          UUID         NOT NULL,
    version     BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    author      VARCHAR(255) NOT NULL,
    author_id   UUID         NOT NULL,
    description VARCHAR(2000),
    CONSTRAINT pk_library_book_details PRIMARY KEY (id)
);
DROP TABLE IF EXISTS library.library_book_previews;
CREATE TABLE library.library_book_previews
(
    book_detail_id UUID         NOT NULL,
    version        BIGINT       NOT NULL DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    title          VARCHAR(255) NOT NULL,
    chapter_count  INT          NOT NULL,
    average_rating FLOAT        NOT NULL,
    rating_count   INT          NOT NULL,
    book_state     TINYINT      NOT NULL,
    CONSTRAINT pk_library_book_previews PRIMARY KEY (book_detail_id)
);
DROP TABLE IF EXISTS library.book_tag;
CREATE TABLE library.book_tag
(
    book_preview_id UUID NOT NULL,
    tag             TINYINT
);
DROP TABLE IF EXISTS library.library_chapter_previews;
CREATE TABLE library.library_chapter_previews
(
    id             UUID   NOT NULL,
    version        BIGINT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    title          VARCHAR(255),
    number         INT    NOT NULL,
    book_detail_id UUID   NOT NULL,
    CONSTRAINT pk_library_chapter_previews PRIMARY KEY (id)
);
DROP TABLE IF EXISTS library.library_chapters;
CREATE TABLE library.library_chapters
(
    chapter_preview_id UUID   NOT NULL,
    version            BIGINT NOT NULL DEFAULT 0,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    text               TEXT,
    CONSTRAINT pk_library_chapters PRIMARY KEY (chapter_preview_id)
);
DROP TABLE IF EXISTS library.library_ratings;
CREATE TABLE library.library_ratings
(
    id             UUID   NOT NULL,
    version        BIGINT NOT NULL DEFAULT 0,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    current_rating INT    NOT NULL,
    user_id        UUID   NOT NULL,
    comment        VARCHAR(255),
    book_detail_id UUID   NOT NULL,
    CONSTRAINT pk_library_ratings PRIMARY KEY (id)
);
DROP TABLE IF EXISTS library.library_user_info;
CREATE TABLE library.library_user_info
(
    id           UUID               NOT NULL,
    version      BIGINT             NOT NULL DEFAULT 0,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    data_updated_at TIMESTAMP,
    username     VARCHAR(20) UNIQUE NOT NULL,
    email        VARCHAR(50) UNIQUE NOT NULL,
    rank         TINYINT            NOT NULL DEFAULT 0,
    user_auth_id UUID               NOT NULL,
    CONSTRAINT pk_library_user_info PRIMARY KEY (id)
);
ALTER TABLE library.library_book_previews
    ADD CONSTRAINT uc_library_bookpreviews_title UNIQUE (title);
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