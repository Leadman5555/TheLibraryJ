package org.library.thelibraryj.authentication.userAuth.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface UserAuthRepository extends BaseJpaRepository<UserAuth, UUID> {
    Optional<UserAuth> findByEmail(String username);
    boolean existsByEmail(String email);
}
