package com.rlabs.crm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Email
    private String email;

    private String password;

    private String firstName;

    private String middleName;

    private String lastName;

    private String phoneNumber;

    private String address;

    private String zipCode;

    private String mfaKey;

    private boolean mfaEnabled;

    private boolean mfaEnforced;

    private String invitedBy;

    private String inviteStatus;

    private String inviteLink;

    private String inviteCode;

    private LocalDateTime inviteSentOn;

    private LocalDateTime inviteAcceptedOn;

    private String tempPassword;

    private Integer loginCount;

    private LocalDateTime lastLoginAt;

    private String profileImageLocation; // local/s3/mongodb/sqldb etc..

    private String profileImageAccessUri; // localpath/s3 bucket/mongodb id/sqldb id etc..

    private String profileImageFileName;

    private boolean enabled;

    private boolean accountLocked;

    private boolean accountExpired;

    private boolean credentialsExpired;

    private String clientIp;

    private String sessionId;

    private boolean isDefault;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToOne(cascade = CascadeType.ALL)
    private User owner;

    public boolean isAccountLocked() {
        return !accountLocked;
    }

    public boolean isAccountExpired() {
        return !accountExpired;
    }

    public boolean isCredentialsExpired() {
        return !credentialsExpired;
    }


}
