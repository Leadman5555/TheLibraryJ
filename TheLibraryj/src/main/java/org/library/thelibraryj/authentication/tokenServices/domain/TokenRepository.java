package org.library.thelibraryj.authentication.tokenServices.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface TokenRepository extends BaseJpaRepository<Token, UUID> {
    @Modifying
    @Query("""
    DELETE FROM token
    WHERE isUsed = true OR expiresAt > CURRENT_TIMESTAMP
""")
    void deleteAllUsedAndExpired();

    Optional<Token> findByToken(UUID token);
}
