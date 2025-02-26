DROP SCHEMA IF EXISTS library CASCADE;
CREATE SCHEMA library;
DROP TABLE IF EXISTS library.library_book_details;
CREATE TABLE library.library_book_details(id          UUID         NOT NULL,version     BIGINT       NOT NULL DEFAULT 0,    created_at  TIMESTAMP,updated_at  TIMESTAMP,author      VARCHAR(255) NOT NULL,author_id   UUID         NOT NULL,description VARCHAR(2000),CONSTRAINT pk_library_book_details PRIMARY KEY (id));
DROP TABLE IF EXISTS library.library_book_previews;
CREATE TABLE library.library_book_previews(book_detail_id UUID         NOT NULL,   version        BIGINT       NOT NULL DEFAULT 0,created_at     TIMESTAMP,updated_at     TIMESTAMP,title          VARCHAR(255) NOT NULL,chapter_count  INT          NOT NULL,average_rating FLOAT        NOT NULL,rating_count   INT          NOT NULL,book_state     TINYINT     NOT NULL,CONSTRAINT pk_library_book_previews PRIMARY KEY (book_detail_id));
DROP TABLE IF EXISTS library.book_tag;
CREATE TABLE library.book_tag(book_preview_id UUID NOT NULL,   tag             TINYINT);
DROP TABLE IF EXISTS library.favourite_books;
CREATE TABLE library.favourite_books(user_info_id UUID NOT NULL,book_id UUID NOT NULL);
DROP TABLE IF EXISTS library.library_chapter_previews;
CREATE TABLE library.library_chapter_previews(id             UUID   NOT NULL,   version        BIGINT NOT NULL DEFAULT 0,created_at     TIMESTAMP,updated_at     TIMESTAMP,title          VARCHAR(255),number         INT    NOT NULL,book_detail_id UUID   NOT NULL,CONSTRAINT pk_library_chapter_previews PRIMARY KEY (id));
DROP TABLE IF EXISTS library.library_chapters;
CREATE TABLE library.library_chapters(chapter_preview_id UUID   NOT NULL,   version            BIGINT NOT NULL DEFAULT 0,created_at         TIMESTAMP,updated_at         TIMESTAMP,text               TEXT,CONSTRAINT pk_library_chapters PRIMARY KEY (chapter_preview_id));
DROP TABLE IF EXISTS library.library_ratings;
CREATE TABLE library.library_ratings(username        VARCHAR(25) UNIQUE NOT NULL, id             UUID   NOT NULL,    version        BIGINT NOT NULL DEFAULT 0,created_at     TIMESTAMP,updated_at     TIMESTAMP,current_rating INT    NOT NULL,user_id        UUID   NOT NULL,comment        VARCHAR(255),book_detail_id UUID   NOT NULL,CONSTRAINT pk_library_ratings PRIMARY KEY (id));
DROP TABLE IF EXISTS library.library_user_info;
CREATE TABLE library.library_user_info(id         UUID   NOT NULL,    version    BIGINT NOT NULL DEFAULT 0,created_at TIMESTAMP,updated_at TIMESTAMP,data_updated_at TIMESTAMP, username   VARCHAR(25) UNIQUE NOT NULL, email      VARCHAR(50) UNIQUE NOT NULL, rank       INTEGER NOT NULL DEFAULT 0, current_score   INTEGER            NOT NULL DEFAULT 0, user_auth_id UUID NOT NULL, status          VARCHAR(300), preference      TINYINT           NOT NULL DEFAULT 0, CONSTRAINT pk_library_user_info PRIMARY KEY (id));
DROP TABLE IF EXISTS library.library_user_auth;
CREATE TABLE  library.library_user_auth(id           UUID               NOT NULL,   version      BIGINT             NOT NULL DEFAULT 0,created_at   TIMESTAMP,updated_at   TIMESTAMP,password VARCHAR(200),email        VARCHAR(50) UNIQUE NOT NULL,role         VARCHAR(12)      NOT NULL,is_enabled BOOLEAN NOT NULL DEFAULT false, is_google  BOOLEAN            NOT NULL,CONSTRAINT pk_library_user_auth PRIMARY KEY (id));
DROP TABLE IF EXISTS library.library_auth_tokens;
CREATE TABLE library.library_auth_tokens(id          UUID      NOT NULL,token       UUID      NOT NULL,for_user_id UUID      NOT NULL,version     BIGINT    NOT NULL DEFAULT 0,created_at  TIMESTAMP,updated_at  TIMESTAMP,expires_at  TIMESTAMP NOT NULL,is_used     BOOLEAN   NOT NULL DEFAULT false,CONSTRAINT pk_library_auth_tokens PRIMARY KEY (id));
DROP TABLE IF EXISTS library.library_book_tokens;
CREATE TABLE library.library_book_tokens(id          UUID      NOT NULL,token       UUID      NOT NULL,for_user_id UUID      NOT NULL,version     BIGINT    NOT NULL DEFAULT 0,created_at  TIMESTAMP,updated_at  TIMESTAMP,expires_at  TIMESTAMP NOT NULL,use_count    INT   NOT NULL DEFAULT 0,CONSTRAINT pk_library_book_tokens PRIMARY KEY (id));
ALTER TABLE library.library_book_previews ADD CONSTRAINT uc_library_bookpreviews_title UNIQUE (title);
ALTER TABLE library.library_book_previews ADD CONSTRAINT fk_library_bookpreviews_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library.library_book_details (id) ON DELETE CASCADE;
ALTER TABLE library.library_chapter_previews    ADD CONSTRAINT fk_library_chapterpreviews_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library.library_book_details (id);
ALTER TABLE library.library_chapters ADD CONSTRAINT fk_library_chapters_on_chapterpreview FOREIGN KEY (chapter_preview_id) REFERENCES library.library_chapter_previews (id) ON DELETE CASCADE;
ALTER TABLE library.library_ratings ADD CONSTRAINT fk_library_ratings_on_bookdetail FOREIGN KEY (book_detail_id) REFERENCES library.library_book_details (id);
ALTER TABLE library.book_tag    ADD CONSTRAINT fk_book_tag_on_book_preview FOREIGN KEY (book_preview_id) REFERENCES library.library_book_previews (book_detail_id) ON DELETE CASCADE;
ALTER TABLE library.favourite_books ADD CONSTRAINT fk_favourite_books_on_user_info FOREIGN KEY (user_info_id) REFERENCES library.library_user_info (id) ON DELETE CASCADE;