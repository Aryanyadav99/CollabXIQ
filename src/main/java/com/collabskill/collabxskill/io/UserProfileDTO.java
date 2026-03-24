package com.collabskill.collabxskill.io;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserProfileDTO {
    private String id;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;          // Gender enum ka string value
    private String profilePictureUrl;
    private String bio;
    private String experienceLevel;  // ExperienceLevel enum
    private String primaryDomain;    // PrimaryDomain enum
    private List<String> techStack;
    private String githubUrl;
    private String linkedinUrl;
}
