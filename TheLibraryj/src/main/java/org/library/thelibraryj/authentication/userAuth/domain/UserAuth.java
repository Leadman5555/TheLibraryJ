package org.library.thelibraryj.authentication.userAuth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.library.thelibraryj.infrastructure.model.AbstractEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Table(name = "library_user_auth")
class UserAuth extends AbstractEntity implements UserDetails {
    @Column(nullable = false)
    private char[] password;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private UserRole role;
    @Column(nullable = false)
    private boolean isEnabled;

    @Builder
    public UserAuth(UUID id, Long version, Instant createdAt, Instant updatedAt, char[] password, String email, UserRole role, boolean isEnabled) {
        super(id, version, createdAt, updatedAt);
        this.password = password;
        this.email = email;
        this.role = role;
        this.isEnabled = isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return new String(password);
    }
}
