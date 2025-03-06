package org.library.thelibraryj.userInfo.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
interface UserInfoRepository extends BaseJpaRepository<UserInfo, UUID>, UserInfoViewRepository {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
            select ui from userInfo ui
            where ui.username = :username
            """)
    Optional<UserInfo> getByUsername(@Param("username") String username);

    @Query("""
            select ui from userInfo ui
            left join fetch ui.favouriteBookIds
            where ui.id = :id
            """)
    Optional<UserInfo> fetchUserInfoEagerById(@Param("id") UUID id);

    @Query("""
            select ui from userInfo ui
            left join fetch ui.favouriteBookIds
            where ui.email = :email
            """)
    Optional<UserInfo> fetchUserInfoEagerByEmail(@Param("email") String email);

    @Query("""
                select id from userInfo
                where email = :email
            """)
    Optional<UUID> getIdByEmail(@Param("email") String email);

    Optional<UserInfo> getByEmail(@Param("email") String email);

    @Query("""
                select username from userInfo
                where email = :email
            """)
    Optional<String> getUsernameByEmail(@Param("email") String email);

    @Query("""
                select createdAt from userInfo
                where email = :email
            """)
    Optional<Instant> getCreatedAtByEmail(@Param("email") String email);

    @Modifying
    @Query("""
                update userInfo ui set ui.currentScore = ui.currentScore + :change
                where ui.id = :userId
            """)
    void updateCurrentScore(@Param("userId") UUID userId, @Param("change") int change);

    @Query(value = "SELECT book_id FROM library.favourite_books WHERE user_info_id = :userId", nativeQuery = true)
    Set<UUID> fetchUserFavouriteBookIds(@Param("userId") UUID userId);

    @Modifying
    @Query(value = "DELETE FROM library.favourite_books WHERE user_info_id = :userId AND book_id = :bookId", nativeQuery = true)
    void removeBookFromFavourites(@Param("userId") UUID userId, @Param("bookId") UUID bookId);

    @Modifying
    @Query(value = "DELETE FROM library.favourite_books WHERE book_id = :bookId", nativeQuery = true)
    void removeBookFromFavouritesForAllUsers(@Param("bookId") UUID bookId);

    @Query(value = "SELECT book_id FROM library.subscribed_books WHERE user_info_email = :email", nativeQuery = true)
    Set<UUID> fetchUserFavouriteSubscribedBookIds(@Param("email") String email);

    @Query(value = "SELECT user_info_email FROM library.subscribed_books WHERE book_id = :bookId", nativeQuery = true)
    Set<String> fetchSubscriberEmailsForBookId(@Param("bookId") UUID bookId);

    @Modifying
    @Query(value = "DELETE FROM library.subscribed_books WHERE book_id = :bookId", nativeQuery = true)
    void removeBookFromSubscribedForAllUsers(@Param("bookId") UUID bookId);

    @Modifying
    @Query(value = "DELETE FROM library.subscribed_books WHERE user_info_email = :email AND book_id = :bookId", nativeQuery = true)
    void removeBookFromSubscribed(@Param("email") String email, @Param("bookId") UUID bookId);
}
