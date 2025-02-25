package org.library.thelibraryj.userInfo.userInfoTokenServices.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.library.thelibraryj.infrastructure.model.entities.Token;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity(name = "book_token")
@NoArgsConstructor
@Table(name = "library_book_tokens")
@EqualsAndHashCode(callSuper = true)
class FavouriteBookToken extends Token {

    @Column(name = "use_count", nullable = false)
    private int useCount;

    @Builder
    public FavouriteBookToken(UUID id, Long version, Instant createdAt, Instant updatedAt, UUID token, Instant expiresAt, UUID forUserId, int useCount) {
        super(id, version, createdAt, updatedAt, token, expiresAt, forUserId);
        this.useCount = useCount;
    }

    @JsonIgnore
    @Transient
    public int incrementUseCount(){
        return ++this.useCount;
    }
}
