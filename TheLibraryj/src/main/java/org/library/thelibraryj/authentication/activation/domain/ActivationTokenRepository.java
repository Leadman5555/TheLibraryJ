package org.library.thelibraryj.authentication.activation.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface ActivationTokenRepository extends BaseJpaRepository<ActivationToken, UUID> {
    @Modifying
    @Query("""
    DELETE FROM activationToken
    WHERE isUsed = true OR expiresAt > CURRENT_TIMESTAMP
""")
    void deleteAllUsedAndExpired();

    Optional<ActivationToken> findByToken(UUID token);
}
