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
    SELECT id, isEnabled FROM userAuth
    WHERE email = :email
""")
    Optional<Object> getBasicUserAuthData(@Param("email") String email);

    @Query("""
    SELECT role, isEnabled, isGoogle FROM userAuth
    WHERE email = :email
""")
    Optional<Object> getLoginData(@Param("email") String email);

    @Query("""
    SELECT id, isGoogle FROM userAuth
    WHERE email = :email
""")
    Optional<Object> getPasswordResetData(@Param("email") String email);

    @Query("""
    SELECT id FROM userAuth WHERE email = :email
""")
    Optional<UUID> getIdByEmail(@Param("email") String email);
}
