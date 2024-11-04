INSERT INTO library.library_book_details (id, version, created_at, updated_at, author, author_id, description)VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER1',        '123e4567-e89b-12d3-a456-426614174000', 'desc');
INSERT INTO library.library_book_previews (book_detail_id, version, created_at, updated_at, title, chapter_count,average_rating, rating_count, book_state) VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Book1', 10, 5, 1, 1);
INSERT INTO library.library_user_info (ID, VERSION, CREATED_AT, UPDATED_AT, DATA_UPDATED_AT , USERNAME, EMAIL, RANK, USER_AUTH_ID) VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP - INTERVAL '100 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP - INTERVAL '100 day','USER1', 'sample.email1@gmail.com', 1, '123e4567-e89b-12d3-a456-426614174000' ) , ('123e4567-e89b-12d3-a456-426614174001', 0, CURRENT_TIMESTAMP - INTERVAL '2 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER2', 'sample.email2@gmail.com', 0, '123e4567-e89b-12d3-a456-426614174001' );
INSERT INTO library.library_user_auth(id, version, created_at, updated_at, password, email, role, is_enabled) VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '$2a$12$TFze7zTS9rzioXQId.v6L.ernC12Rf0p5uCMyvaAGhxdtyRlzfOvu', 'sample.email1@gmail.com', 'ADMIN', true), ('123e4567-e89b-12d3-a456-426614174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '$2a$12$TFze7zTS9rzioXQId.v6L.ernC12Rf0p5uCMyvaAGhxdtyRlzfOvu', 'sample.email2@gmail.com', 'ADMIN', false);
INSERT INTO library.book_tag(BOOK_PREVIEW_ID, TAG) VALUES ( '123e4567-e89b-12d3-a456-426614174000',  1);
INSERT INTO library.library_chapter_previews(ID, VERSION, CREATED_AT, UPDATED_AT, TITLE, NUMBER, BOOK_DETAIL_ID) VALUES ( '123e4567-e89b-12d3-a456-999994174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title', 1, '123e4567-e89b-12d3-a456-426614174000' );
INSERT INTO library.library_chapters(CHAPTER_PREVIEW_ID, VERSION, CREATED_AT, UPDATED_AT, TEXT) VALUES ('123e4567-e89b-12d3-a456-999994174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'this is a long chapter text'  );
INSERT INTO library.library_ratings(ID, VERSION, CREATED_AT, UPDATED_AT, CURRENT_RATING, USER_ID, COMMENT, BOOK_DETAIL_ID) VALUES ('123e4567-e89b-12d3-a456-888884174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, '123e4567-e89b-12d3-a456-426614174000', 'some comment', '123e4567-e89b-12d3-a456-426614174000');


-- password for all users is 'password' (without the quote marks)