package com.rlabs.crm.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rlabs.crm.domain.Role;
import com.rlabs.crm.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String username;

    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Collection<? extends Role> roles;

    private boolean enabled;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean mfaEnabled;

    private Integer loginCount;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdOn;
//    private User user;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities,
                           Collection<? extends Role> roles,
                           boolean enabled,
                           boolean accountNonLocked,
                           boolean accountNonExpired,
                           boolean credentialsNonExpired,
                           boolean mfaEnabled,
                           Integer loginCount,
                           LocalDateTime lastLoginAt,
                           LocalDateTime createdOn) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.roles = roles;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.mfaEnabled = mfaEnabled;
        this.loginCount = loginCount;
        this.lastLoginAt = lastLoginAt;
        this.createdOn = createdOn;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());

        return new UserDetailsImpl(user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            authorities,
            user.getRoles(),
            user.isEnabled(),
            user.isAccountLocked(),
            user.isAccountExpired(),
            user.isCredentialsExpired(),
            user.isMfaEnabled(),
            user.getLoginCount(),
            user.getLastLoginAt(),
            user.getCreatedOn()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public Collection<? extends Role> getRoles() {
        return roles;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
