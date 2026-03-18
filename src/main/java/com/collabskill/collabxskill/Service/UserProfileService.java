package com.collabskill.collabxskill.Service;

import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.extra.UserProfileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserProfileService {
    void saveUserProfileWithRandomImage(UserProfile profile) throws IOException;
    void saveProfile(UserProfileDTO profile, MultipartFile photo, String userId);
}
