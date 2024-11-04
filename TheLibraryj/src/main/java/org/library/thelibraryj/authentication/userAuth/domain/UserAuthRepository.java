package org.library.thelibraryj.authentication.userAuth.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface UserAuthRepository extends BaseJpaRepository<UserAuth, UUID> {
    Optional<UserAuth> findByEmail(String username);
    boolean existsByEmail(String email);
    @Query("""
    SELECT u.isEnabled FROM UserAuth u
    where u.id = :id
    """)
    Optional<Boolean> isEnabled(@Param("id") UUID id);
}
