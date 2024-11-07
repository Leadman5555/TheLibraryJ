package org.library.thelibraryj.userInfo.domain;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface UserInfoRepository extends BaseJpaRepository<UserInfo, UUID> {
    boolean existsByUsername(String username);
}
