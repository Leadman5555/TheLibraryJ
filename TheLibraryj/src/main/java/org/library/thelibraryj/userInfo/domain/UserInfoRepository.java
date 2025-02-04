package org.library.thelibraryj.userInfo.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
interface UserInfoRepository extends BaseJpaRepository<UserInfo, UUID>, UserInfoViewRepository {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserInfo> getByUsername(@Param("username") String username);

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
}
