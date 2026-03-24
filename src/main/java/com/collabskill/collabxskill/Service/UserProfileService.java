package com.collabskill.collabxskill.Service;

import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.io.UserProfileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserProfileService {
    void saveUserProfileWithRandomImage(UserProfile profile) throws IOException;
    void saveProfile(UserProfileDTO profile, MultipartFile photo, String userId);

    UserProfileDTO getUserProfileById(String userId);

    List<UserProfileDTO> getOtherProfiles(String id, int limit);
}
