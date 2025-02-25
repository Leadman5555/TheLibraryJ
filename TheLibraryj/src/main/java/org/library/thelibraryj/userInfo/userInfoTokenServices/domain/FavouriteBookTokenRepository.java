package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface FavouriteBookTokenRepository extends BaseJpaRepository<FavouriteBookToken, UUID> {
    @Modifying
    @Query("""
                DELETE FROM book_token
                WHERE expiresAt < CURRENT_TIMESTAMP
            """)
    void deleteAllUsedAndExpired();

    Optional<FavouriteBookToken> findByToken(UUID token);

    @Modifying
    @Query("""
            update book_token bt SET bt.useCount = (bt.useCount + :change)
            where bt.token = :token
            """)
    Optional<FavouriteBookToken> incrementTokenUsage(@Param("token") UUID token, @Param("change") int change);
}
