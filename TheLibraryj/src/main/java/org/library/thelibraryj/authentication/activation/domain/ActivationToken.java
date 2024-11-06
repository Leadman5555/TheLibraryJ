package org.library.thelibraryj.authentication.activation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "activationToken")
@NoArgsConstructor
@Table(name = "library_activation_tokens")
@EqualsAndHashCode(callSuper = true)
class ActivationToken extends AbstractEntity {
    @Column(nullable = false)
    @Getter
    private UUID token;
    @Column(nullable = false)
    @Getter
    private Instant expiresAt;
    @Column(nullable = false)
    @Getter
    private UUID forUserId;
    @Column(nullable = false)
    @Setter
    private boolean isUsed;

    @Builder
    public ActivationToken(UUID id, Long version, Instant createdAt, Instant updatedAt, UUID token, Instant expiresAt, UUID forUserId, boolean isUsed) {
        super(id, version, createdAt, updatedAt);
        this.token = token;
        this.expiresAt = expiresAt;
        this.forUserId = forUserId;
        this.isUsed = isUsed;
    }

    @Transient
    public boolean hasExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return isUsed;
    }
}
