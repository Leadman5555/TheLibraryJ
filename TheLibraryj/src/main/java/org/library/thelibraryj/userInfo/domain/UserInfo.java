package org.library.thelibraryj.userInfo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
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

    @Column(nullable = false)
    private Instant dataUpdatedAt;

    @Column(nullable = false, unique = true)
    @Size(min = 5, max = 20)
    private String username;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Min(0)
    @Max(10)
    @Column(nullable = false)
    private int rank;

    @Column(nullable = false)
    private UUID userAuthId;

    @Builder
    public UserInfo(UUID id, Long version, Instant createdAt, Instant updatedAt, Instant dataUpdatedAt, String username, String email, int rank, UUID userAuthId) {
        super(id, version, createdAt, updatedAt);
        this.dataUpdatedAt = dataUpdatedAt;
        this.username = username;
        this.email = email;
        this.rank = rank;
        this.userAuthId = userAuthId;
    }
}
