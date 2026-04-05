package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.Service.UserProfileService;
import com.collabskill.collabxskill.extra.*;
import com.collabskill.collabxskill.io.UserProfileDTO;
import com.collabskill.collabxskill.repo.UserActionRepository;
import com.collabskill.collabxskill.repo.UserProfileRepo;
import com.collabskill.collabxskill.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepo userProfileRepository;
    private final ModelMapper modelMapper;
    private final UserActionRepository userActionRepository;
    @Override
    public void saveUserProfileWithRandomImage(UserProfile profile) throws IOException {

    }

    @Transactional
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

    @Override
    public UserProfileDTO getUserProfileById(String userId) {
        Optional<UserProfile> userProfile= userProfileRepository.findByUserId(userId);
        if(userProfile.isEmpty()){
            return null;
        }
        UserProfile profile=userProfile.get();
        return modelMapper.map(profile,UserProfileDTO.class);
    }

    @Override
    public List<UserProfileDTO> getOtherProfiles(String id, int limit) {
            Optional<UserProfile> profile = userProfileRepository.findByUser_Id(id);
            if (profile.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Create your profile first to explore others");
            }

            // Blocked users ki list
            List<String> blockedByMe = userActionRepository
                    .findByFromUser_IdAndActionType(id, ActionType.BLOCK)
                    .stream()
                    .map(action -> action.getToUser().getId())
                    .toList();

            // Bloked by Me
            List<String> blockedMe = userActionRepository
                    .findByToUser_IdAndActionType(id, ActionType.BLOCK)
                    .stream()
                    .map(action -> action.getFromUser().getId())
                    .toList();

            // Already interacted
            List<String> alreadyActed = userActionRepository
                    .findByFromUser_Id(id)
                    .stream()
                    .map(action -> action.getToUser().getId())
                    .toList();

            List<UserProfile> allProfiles = userProfileRepository.findAll();

            return allProfiles.stream()
                    .filter(p -> !p.getUser().getId().equals(id))
                    .filter(p -> !blockedByMe.contains(p.getUser().getId()))
                    .filter(p -> !blockedMe.contains(p.getUser().getId()))
                    .filter(p -> !alreadyActed.contains(p.getUser().getId()))
                    .sorted(Comparator.comparingDouble(other ->
                            -calculateScore(profile, other)))
                    .limit(limit)
                    .map(p -> {
                        UserProfileDTO dto = modelMapper.map(p, UserProfileDTO.class);
                        dto.setId(p.getUser().getId()); // User ka id set karo
                        return dto;
                    })
                    .collect(toList());
    }

    private double calculateScore(Optional<UserProfile> current1, UserProfile other) {
        double score = 0;
        if(current1.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Create your profile first to explore others");
        }
        UserProfile current=current1.get();
        // 1. Domain — 40 pts
        if (current.getPrimaryDomain() != null && other.getPrimaryDomain() != null) {
            if (current.getPrimaryDomain() == other.getPrimaryDomain()) {
                score += 40;
            }
        }

        // 2. TechStack — 10 pts per match
        if (current.getTechStack() != null && other.getTechStack() != null) {
            long commonCount = current.getTechStack().stream()
                    .filter(t -> other.getTechStack().stream()
                            .anyMatch(ot -> ot.equalsIgnoreCase(t)))
                    .count();
            score += commonCount * 10;
        }

        // 3. Experience — 20 pts same, 10 pts different
        if (current.getExperienceLevel() != null && other.getExperienceLevel() != null) {
            if (current.getExperienceLevel() == other.getExperienceLevel()) {
                score += 20;
            } else {
                score += 10;
            }
        }

        return score;
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
