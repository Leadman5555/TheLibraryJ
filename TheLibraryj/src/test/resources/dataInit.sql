INSERT INTO library.library_book_details (id, version, created_at, updated_at, author, author_id, description)VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER1',        '123e4567-e89b-12d3-a456-426614174000', 'desc'),('123e4567-e89b-12d3-a456-426614174003', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER1',        '123e4567-e89b-12d3-a456-426614174000', 'desc'), ('123e4567-e89b-12d3-a456-426614174006', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER3',   '123e4567-e89b-12d3-a456-426614174002', 'desc6');
INSERT INTO library.library_book_previews (book_detail_id, version, created_at, updated_at, title, chapter_count,average_rating, rating_count, book_state) VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Book1', 10, 5, 1, 1),('123e4567-e89b-12d3-a456-426614174003', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CHAPTER_BOOK', 0, 5, 1, 1), ('123e4567-e89b-12d3-a456-426614174006', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Book7D', 500, 3, 13, 2);
INSERT INTO library.library_user_info (ID, VERSION, CREATED_AT, UPDATED_AT, DATA_UPDATED_AT, STATUS ,USERNAME, EMAIL, RANK,CURRENT_SCORE, USER_AUTH_ID, PREFERENCE) VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP -  100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP- 100, 'status1' ,'USER1', 'sample.email1@gmail.com', 1, 0, '123e4567-e89b-12d3-a456-426614174000' , 1) , ('123e4567-e89b-12d3-a456-426614174001', 0, CURRENT_TIMESTAMP - 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'status2' ,'USER2', 'sample.email2@gmail.com', 0,  0,'123e4567-e89b-12d3-a456-426614174001', 0 ), ('123e4567-e89b-12d3-a456-426614174002', 0, CURRENT_TIMESTAMP - 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'status3' ,'USER3', 'sample.email3@gmail.com', 0,  0,'123e4567-e89b-12d3-a456-426614174002', 0 );
INSERT INTO library.library_user_auth(id, version, created_at, updated_at, password, email, role, is_enabled, is_google) VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '$2a$12$TFze7zTS9rzioXQId.v6L.ernC12Rf0p5uCMyvaAGhxdtyRlzfOvu', 'sample.email1@gmail.com', 'ROLE_ADMIN', true, false), ('123e4567-e89b-12d3-a456-426614174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '$2a$12$TFze7zTS9rzioXQId.v6L.ernC12Rf0p5uCMyvaAGhxdtyRlzfOvu', 'sample.email2@gmail.com', 'ROLE_ADMIN', false, false), ('123e4567-e89b-12d3-a456-426614174002', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'sample.email3@gmail.com', 'ROLE_USER', true, true);
INSERT INTO library.book_tag(BOOK_PREVIEW_ID, TAG) VALUES ( '123e4567-e89b-12d3-a456-426614174000',  1),  ( '123e4567-e89b-12d3-a456-426614174003',  1),      ('123e4567-e89b-12d3-a456-426614174006', 2),('123e4567-e89b-12d3-a456-426614174006', 4),        ('123e4567-e89b-12d3-a456-426614174006', 2),('123e4567-e89b-12d3-a456-426614174006', 1);;
INSERT INTO library.library_chapter_previews(ID, VERSION, CREATED_AT, UPDATED_AT, TITLE, NUMBER, BOOK_DETAIL_ID) VALUES ( '123e4567-e89b-12d3-a456-999994174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title', 1, '123e4567-e89b-12d3-a456-426614174000' );
INSERT INTO library.library_chapters(CHAPTER_PREVIEW_ID, VERSION, CREATED_AT, UPDATED_AT, TEXT) VALUES ('123e4567-e89b-12d3-a456-999994174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'this is a long chapter text'  );
INSERT INTO library.library_ratings(ID, VERSION, CREATED_AT, UPDATED_AT, CURRENT_RATING, USER_ID, USERNAME, COMMENT, BOOK_DETAIL_ID) VALUES ('123e4567-e89b-12d3-a456-888884174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, '123e4567-e89b-12d3-a456-426614174000', 'USER1' , 'some comment', '123e4567-e89b-12d3-a456-426614174000');