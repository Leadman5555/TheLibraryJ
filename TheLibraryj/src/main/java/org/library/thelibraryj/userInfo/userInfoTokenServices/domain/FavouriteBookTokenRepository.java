package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface FavouriteBookTokenRepository extends BaseJpaRepository<FavouriteBookToken, UUID> {

    @Modifying
    @Query("""
                DELETE FROM book_token
                WHERE expiresAt < CURRENT_TIMESTAMP
            """)
    void deleteAllExpired();

    @Async
    @Modifying
    @Query("""
            update book_token bt SET bt.useCount = (bt.useCount + :change)
            where bt.token = :token
            """)
    void incrementTokenUsage(@Param("token") UUID token, @Param("change") int change);

    @Query("""
                select (count(bt) > 0) from book_token bt where bt.token = :token and bt.forUserId = :userId
            """)
    boolean existsByTokenAndForUserId(@Param("token") UUID token, @Param("userId") UUID userId);
}
