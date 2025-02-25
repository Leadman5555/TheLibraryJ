INSERT INTO library.library_book_details (id, version, created_at, updated_at, author, author_id, description)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER1',
        '123e4567-e89b-12d3-a456-426614174000', 'Humans are clever in tens of thousands of ways, Gu are the true refined essences of Heaven and Earth.

The Three Temples are unrighteous, the demon is reborn.

Former days are but an old dream, an identical name is made anew.

A story of a time traveler who keeps on being reborn.

A unique world that grows, cultivates, and uses Gu.

The Spring and Autumn Cicada, the Venomous Moonlight Gu, the Wine Insect, All-Encompassing Golden Light Insect, Slender Black Hair Gu, Gu of Hope...

And a great demon of the world that does exactly as his heart pleases!

—

*Gu (蛊) referred to poison in various cultures in South China while in folklore Gu spirits could transform into all kinds of species.'),
       ('123e4567-e89b-12d3-a456-426614174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER1',
        '123e4567-e89b-12d3-a456-426614174000', 'desc1'),
       ('123e4567-e89b-12d3-a456-426614174002', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER1',
        '123e4567-e89b-12d3-a456-426614174000', 'desc2'),
       ('123e4567-e89b-12d3-a456-426614174003', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER1',
        '123e4567-e89b-12d3-a456-426614174000', 'desc3'),
       ('123e4567-e89b-12d3-a456-426614174004', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER1',
        '123e4567-e89b-12d3-a456-426614174000', 'desc4'),
       ('123e4567-e89b-12d3-a456-426614174005', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER3',
        '123e4567-e89b-12d3-a456-426614174002', 'desc5'),
       ('123e4567-e89b-12d3-a456-426614174006', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'USER3',
        '123e4567-e89b-12d3-a456-426614174002', 'desc6');
INSERT INTO library.library_book_previews (book_detail_id, version, created_at, updated_at, title, chapter_count,
                                           average_rating, rating_count, book_state)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Reverend Insanity', 10, 5, 1, 1),
        ('123e4567-e89b-12d3-a456-426614174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Book2D', 645, 4, 10, 2),
        ('123e4567-e89b-12d3-a456-426614174002', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Book3D', 450, 10, 100, 1),
        ('123e4567-e89b-12d3-a456-426614174003', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Book4D', 100, 0, 0, 4),
        ('123e4567-e89b-12d3-a456-426614174004', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Book5D', 1002, 1, 10, 3),
        ('123e4567-e89b-12d3-a456-426614174005', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Book6D', 104, 4, 64, 1),
        ('123e4567-e89b-12d3-a456-426614174006', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Book7D', 500, 3, 13, 2);
INSERT INTO library.library_user_info (ID, VERSION, CREATED_AT, UPDATED_AT, DATA_UPDATED_AT, STATUS ,USERNAME, EMAIL, RANK,
                                       CURRENT_SCORE, USER_AUTH_ID, PREFERENCE)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP - INTERVAL '100 day', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP - INTERVAL '100 day', 'status1' ,'USER1', 'sample.email1@gmail.com', 1, 5,
        '123e4567-e89b-12d3-a456-426614174000', 1),
       ('123e4567-e89b-12d3-a456-426614174001', 0, CURRENT_TIMESTAMP - INTERVAL '2 day', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'status2' ,'USER2', 'sample.email2@gmail.com', 0, 1, '123e4567-e89b-12d3-a456-426614174001', 0),
       ('123e4567-e89b-12d3-a456-426614174002', 0, CURRENT_TIMESTAMP - INTERVAL '2 day', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, NULL ,'USER_GOOGLE3', 'sample.email3@gmail.com', 0, 0, '123e4567-e89b-12d3-a456-426614174002', 0);
INSERT INTO library.library_user_auth(id, version, created_at, updated_at, password, email, role, is_enabled, is_google)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        '$2a$12$TFze7zTS9rzioXQId.v6L.ernC12Rf0p5uCMyvaAGhxdtyRlzfOvu', 'sample.email1@gmail.com', 'ROLE_ADMIN', true,
        false),
       ('123e4567-e89b-12d3-a456-426614174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        '$2a$12$TFze7zTS9rzioXQId.v6L.ernC12Rf0p5uCMyvaAGhxdtyRlzfOvu', 'sample.email2@gmail.com', 'ROLE_USER', true, false),
       ('123e4567-e89b-12d3-a456-426614174002', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL,
        'sample.email3@gmail.com', 'ROLE_ADMIN', true, true);
INSERT INTO library.book_tag(BOOK_PREVIEW_ID, TAG)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 1),
       ('123e4567-e89b-12d3-a456-426614174001', 2),
       ('123e4567-e89b-12d3-a456-426614174001', 3),
       ('123e4567-e89b-12d3-a456-426614174002', 1),
       ('123e4567-e89b-12d3-a456-426614174002', 2),
       ('123e4567-e89b-12d3-a456-426614174002', 3),
       ('123e4567-e89b-12d3-a456-426614174003', 1),
       ('123e4567-e89b-12d3-a456-426614174003', 6),
       ('123e4567-e89b-12d3-a456-426614174004', 5),
       ('123e4567-e89b-12d3-a456-426614174005', 1),
       ('123e4567-e89b-12d3-a456-426614174005', 5),
       ('123e4567-e89b-12d3-a456-426614174006', 2),
       ('123e4567-e89b-12d3-a456-426614174006', 4);
INSERT INTO library.library_chapter_previews(ID, VERSION, CREATED_AT, UPDATED_AT, TITLE, NUMBER, BOOK_DETAIL_ID)
VALUES ('123e4567-e89b-12d3-a456-999994174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title1', 1,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title2', 2,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174002', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title3', 3,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174003', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title4', 4,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174004', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title5', 5,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174005', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title6', 6,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174006', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title7', 7,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174007', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title8', 8,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174008', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title9', 9,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174009', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title10', 10,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174010', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'chapter title11', 11,
        '123e4567-e89b-12d3-a456-426614174000'),
       ('123e4567-e89b-12d3-a456-999994174011', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Three Venerables Attack Heavenly Court', 2333,
        '123e4567-e89b-12d3-a456-426614174000');
INSERT INTO library.library_chapters(CHAPTER_PREVIEW_ID, VERSION, CREATED_AT, UPDATED_AT, TEXT)
VALUES ('123e4567-e89b-12d3-a456-999994174000', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'this is a long chapter text');
INSERT INTO library.library_ratings(ID, VERSION, CREATED_AT, UPDATED_AT, CURRENT_RATING, USER_ID, USERNAME, COMMENT,
                                    BOOK_DETAIL_ID)
VALUES ('123e4567-e89b-12d3-a456-888884174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5,
        '123e4567-e89b-12d3-a456-426614174000', 'USER1', 'some comment', '123e4567-e89b-12d3-a456-426614174000');
INSERT INTO library.library_auth_tokens(id, token, for_user_id, version, created_at, updated_at, expires_at, is_used)
VALUES ('123e4567-e89b-12d3-a456-426614174001', '123e4567-e89b-12d3-a456-426614174001',
        '123e4567-e89b-12d3-a456-426614174001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP + INTERVAL '10 day', false);
-- password for all users is 'password' (without the quote marks)