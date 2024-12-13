package org.library.thelibraryj.userInfo.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface UserInfoRepository extends BaseJpaRepository<UserInfo, UUID>, UserInfoViewRepository {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserInfo> getByUsername(@Param("username") String username);

    @Query("""
                select u.id from userInfo u
                where u.email = :email
            """)
    Optional<UUID> getIdByEmail(@Param("email") String email);

    Optional<UserInfo> getByEmail(@Param("email") String email);
}
