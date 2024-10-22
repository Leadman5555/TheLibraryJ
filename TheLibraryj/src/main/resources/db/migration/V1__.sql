CREATE TABLE book_tag
(
    book_preview_id UUID NOT NULL,
    tag             TINYINT
);

CREATE TABLE library_book_details
(
    id          UUID         NOT NULL,
    version     BIGINT       NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    author      VARCHAR(255) NOT NULL,
    author_id   UUID         NOT NULL,
    description VARCHAR(2000),
    CONSTRAINT pk_library_book_details PRIMARY KEY (id)
);

CREATE TABLE library_book_previews
(
    book_detail_id UUID         NOT NULL,
    version        BIGINT       NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    title          VARCHAR(255) NOT NULL,
    chapter_count  INT          NOT NULL,
    average_rating FLOAT        NOT NULL,
    rating_count   INT          NOT NULL,
    book_state     TINYINT     NOT NULL,
    CONSTRAINT pk_library_book_previews PRIMARY KEY (book_detail_id)
);

CREATE TABLE library_chapter_previews
(
    id             UUID   NOT NULL,
    version        BIGINT NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    title          VARCHAR(255),
    number         INT    NOT NULL,
    book_detail_id UUID   NOT NULL,
    CONSTRAINT pk_library_chapter_previews PRIMARY KEY (id)
);

CREATE TABLE library_chapters
(
    chapter_preview_id UUID   NOT NULL,
    version            BIGINT NOT NULL,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    text               TEXT,
    CONSTRAINT pk_library_chapters PRIMARY KEY (chapter_preview_id)
);

CREATE TABLE library_ratings
(
    id             UUID   NOT NULL,
    version        BIGINT NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    current_rating INT    NOT NULL,
    user_id        UUID   NOT NULL,
    comment        VARCHAR(255),
    book_detail_id UUID   NOT NULL,
    CONSTRAINT pk_library_ratings PRIMARY KEY (id)
);

CREATE TABLE library_user_info
(
    id         UUID   NOT NULL,
    version    BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT pk_library_user_info PRIMARY KEY (id)
);

ALTER TABLE library_book_previews
    ADD CONSTRAINT uc_library_bookpreviews_title UNIQUE (title);

ALTER TABLE library_book_previews
    ADD CONSTRAINT fk_library_bookpreviews_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library_book_details (id);

ALTER TABLE library_chapter_previews
    ADD CONSTRAINT fk_library_chapterpreviews_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library_book_details (id);

ALTER TABLE library_chapters
    ADD CONSTRAINT fk_library_chapters_on_chapterpreview FOREIGN KEY (chapter_preview_id) REFERENCES library_chapter_previews (id);

ALTER TABLE library_ratings
    ADD CONSTRAINT fk_library_ratings_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library_book_details (id);

ALTER TABLE book_tag
    ADD CONSTRAINT fk_book_tag_on_book_preview FOREIGN KEY (book_preview_id) REFERENCES library_book_previews (book_detail_id);