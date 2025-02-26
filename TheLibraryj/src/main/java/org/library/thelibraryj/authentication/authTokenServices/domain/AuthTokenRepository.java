package org.library.thelibraryj.authentication.authTokenServices.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface AuthTokenRepository extends BaseJpaRepository<AuthToken, UUID> {
    @Modifying
    @Query("""
                DELETE FROM auth_token
                WHERE expiresAt < CURRENT_TIMESTAMP
            """)
    void deleteAllExpired();

    Optional<AuthToken> findByToken(UUID token);
}
