package org.library.thelibraryj.userInfo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.library.thelibraryj.infrastructure.model.entities.AbstractEntity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
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
    private short rank;

    @Column(nullable = false)
    private int currentScore;

    @Column(nullable = false)
    @Min(0)
    @Max(110)
    private short preference;

    @Column
    @Size(max = 300)
    private String status;

    @Column(nullable = false)
    private UUID userAuthId;

    @JsonIgnore
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "favourite_books", joinColumns =  @JoinColumn(name = "user_info_id"))
    @Column(name = "book_id")
    private Set<UUID> favouriteBookIds = new HashSet<>();

    @JsonIgnore
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "subscribed_books", joinColumns =  @JoinColumn(name = "user_info_email", referencedColumnName = "email"))
    @Column(name = "book_id")
    private Set<UUID> subscribedBookIds = new HashSet<>();

    @Builder
    public UserInfo(UUID id, Long version, Instant createdAt, Instant updatedAt, Instant dataUpdatedAt, String username, String email, short rank, UUID userAuthId, int currentScore, short preference, String status) {
        super(id, version, createdAt, updatedAt);
        this.dataUpdatedAt = dataUpdatedAt;
        this.username = username;
        this.email = email;
        this.rank = rank;
        this.userAuthId = userAuthId;
        this.currentScore = currentScore;
        this.preference = preference;
        this.status = status;
    }

    @Transient
    @JsonIgnore
    public int addBookIdToFavourites(UUID bookId){
        this.favouriteBookIds.add(bookId);
        return this.favouriteBookIds.size();
    }

    @Transient
    @JsonIgnore
    public int addBookIdToFavourites(Set<UUID> bookIds){
        this.favouriteBookIds.addAll(bookIds);
        return this.favouriteBookIds.size();
    }

    @Transient
    @JsonIgnore
    public int addBookIdToSubscribed(UUID bookId){
        this.subscribedBookIds.add(bookId);
        return this.subscribedBookIds.size();
    }

    @Transient
    @JsonIgnore
    public int addBookIdToSubscribed(Set<UUID> bookIds){
        this.subscribedBookIds.addAll(bookIds);
        return this.subscribedBookIds.size();
    }
}
