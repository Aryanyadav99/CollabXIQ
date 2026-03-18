package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.Service.UserProfileService;
import com.collabskill.collabxskill.extra.ExperienceLevel;
import com.collabskill.collabxskill.extra.Gender;
import com.collabskill.collabxskill.extra.PrimaryDomain;
import com.collabskill.collabxskill.extra.UserProfileDTO;
import com.collabskill.collabxskill.repo.UserProfileRepo;
import com.collabskill.collabxskill.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepo userProfileRepository;
    @Override
    public void saveUserProfileWithRandomImage(UserProfile profile) throws IOException {

    }

    @Override
    public void saveProfile(UserProfileDTO profileDTO, MultipartFile photo, String userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check karo profile already hai ya nahi
        Optional<UserProfile> existingProfile = userProfileRepository.findByUserId(userId);

        UserProfile profile;
        if (existingProfile.isPresent()) {
            // Update existing profile
            profile = existingProfile.get();
        } else {
            // Naya banao
            profile = new UserProfile();
            profile.setUser(userEntity);
        }

        // Fields update karo
        if (profileDTO.getName() != null) profile.setName(profileDTO.getName());
        if (profileDTO.getDateOfBirth() != null) profile.setDateOfBirth(profileDTO.getDateOfBirth());
        if (profileDTO.getBio() != null) profile.setBio(profileDTO.getBio());
        if (profileDTO.getGithubUrl() != null) profile.setGithubUrl(profileDTO.getGithubUrl());
        if (profileDTO.getLinkedinUrl() != null) profile.setLinkedinUrl(profileDTO.getLinkedinUrl());
        if (profileDTO.getTechStack() != null) profile.setTechStack(profileDTO.getTechStack());

        // Enum fields
        if (profileDTO.getGender() != null) {
            profile.setGender(Gender.valueOf(profileDTO.getGender().toUpperCase()));
        }
        if (profileDTO.getPrimaryDomain() != null) {
            profile.setPrimaryDomain(PrimaryDomain.valueOf(profileDTO.getPrimaryDomain().toUpperCase()));
        }
        if (profileDTO.getExperienceLevel() != null) {
            profile.setExperienceLevel(ExperienceLevel.valueOf(profileDTO.getExperienceLevel().toUpperCase()));
        }

        // Photo upload — sirf agar naya photo aaya ho
        if (photo != null && !photo.isEmpty()) {
            String uploadedUrl = uploadFile(photo);
            if (uploadedUrl != null && !uploadedUrl.isEmpty()) {
                profile.setProfilePictureUrl(uploadedUrl);
            }
        }

        userProfileRepository.save(profile);
    }
    @Value("${file.upload-dir}")
    private String uploadDir;

    private String uploadFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName; // URL jo return hoga
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            return null;
        }
    }
}
