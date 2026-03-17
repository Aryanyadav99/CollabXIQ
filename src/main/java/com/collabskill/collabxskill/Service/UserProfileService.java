package com.collabskill.collabxskill.Service;

import com.collabskill.collabxskill.Entities.UserProfile;

import java.io.IOException;

public interface UserProfileService {
    void saveUserProfileWithRandomImage(UserProfile profile) throws IOException;
}
