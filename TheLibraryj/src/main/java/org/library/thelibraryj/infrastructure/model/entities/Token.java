package org.library.thelibraryj.infrastructure.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class Token extends AbstractEntity {

    @Column(nullable = false)
    private UUID token;
    @Column(nullable = false)
    private Instant expiresAt;
    @Column(nullable = false)
    private UUID forUserId;

    public Token(UUID id, Long version, Instant createdAt, Instant updatedAt, UUID token, Instant expiresAt, UUID forUserId) {
        super(id, version, createdAt, updatedAt);
        this.token = token;
        this.expiresAt = expiresAt;
        this.forUserId = forUserId;
    }

    @JsonIgnore
    @Transient
    public boolean hasExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}

