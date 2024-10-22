package org.library.thelibraryj.userInfo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity(name = "userInfo")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "library_user_info")
class UserInfo extends AbstractEntity {
    public UserInfo(UUID id, Long version, Instant createdAt, Instant updatedAt) {
        super(id, version, createdAt, updatedAt);
    }
}
