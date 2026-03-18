package com.collabskill.collabxskill.Entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // here we are using uuid because it's  safe to use not predictable
    private String id;

    // here I set the token version so we can invalidate JWT before expiry like by doing logout
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer tokenVersion = 0;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @JsonIgnore
    private String otp;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private UserProfile userProfile;

    // BAAD MEIN — change kar aise
    @Column(name = "is_admin", nullable = false, columnDefinition = "boolean default false")
    private boolean isAdmin = false;

    @Column(name = "is_verified", nullable = false, columnDefinition = "boolean default false")
    private boolean isVerified = false;

    @Column(name = "is_banned", nullable = false, columnDefinition = "boolean default false")
    private boolean isBanned = false;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    @JsonIgnore
    public void updateTimestamp() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
    }

}
