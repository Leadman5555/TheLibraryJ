package org.library.thelibraryj.authentication.authTokenServices.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.library.thelibraryj.infrastructure.tokenServices.Token;

import java.time.Instant;
import java.util.UUID;

@Setter
@Entity(name = "auth_token")
@NoArgsConstructor
@Table(name = "library_auth_tokens")
@EqualsAndHashCode(callSuper = true)
class AuthToken extends Token {

    @Column(name = "is_used", nullable = false)
    private boolean isUsed;

    @Builder
    public AuthToken(UUID id, Long version, Instant createdAt, Instant updatedAt, UUID token, Instant expiresAt, UUID forUserId, boolean isUsed) {
        super(id, version, createdAt, updatedAt, token, expiresAt, forUserId);
        this.isUsed = isUsed;
    }

    @Transient
    @JsonIgnore
    public boolean isUsed() {
        return isUsed;
    }
}

