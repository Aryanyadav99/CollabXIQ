package com.collabskill.collabxskill.Entities;

import com.collabskill.collabxskill.extra.ExperienceLevel;
import com.collabskill.collabxskill.extra.Gender;
import com.collabskill.collabxskill.extra.PrimaryDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="userProfile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private  User user;

    @Column
    private String name;

    private LocalDate dateOfBirth; // help to compute age dynamically

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "profile_picture_url", length = 2048)
    private String profilePictureUrl = "default-avatar.png";

    @Column(length = 1000)
    private String bio;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;
    // Beginner / Intermediate / Advanced

    @Enumerated(EnumType.STRING)
    private PrimaryDomain primaryDomain;
    // Backend, Frontend, AI, Blockchain, DSA etc.

    @ElementCollection
    //hibernate will create a separate table to store the list of tech stack for each user profile
    private List<String> techStack;
    // which tech stack are you using like put Node.js,react,springboot etc

    private String githubUrl;

    private String linkedinUrl;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

}