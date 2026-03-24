package com.collabskill.collabxskill.Contoller;

import com.collabskill.collabxskill.Service.UserProfileService;
import com.collabskill.collabxskill.Service.UserService;
import com.collabskill.collabxskill.extra.Constants;
import com.collabskill.collabxskill.io.UserProfileDTO;
import com.collabskill.collabxskill.io.UserResponseDTO;
import com.collabskill.collabxskill.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/userProfiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final UserProfileService userProfileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?>addUserProfile(
            @RequestPart("profile")UserProfileDTO userProfile,
            @RequestParam("userId") String userId,
            @RequestPart(value = "photo", required = false) MultipartFile photo
            ){
        if(userId==null || userId.isEmpty()){
            return ResponseEntity.badRequest().body("User ID is required");
        }
        UserResponseDTO userResponseDTO=userService.getUserById(userId);
        UserResponseDTO currentUserDTO=securityUtil.getCurrentUserDto();
        if(userResponseDTO!=null && currentUserDTO!=null){
            if(!(userResponseDTO.getId().equals(currentUserDTO.getId()))){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, Constants.ACCESS_DENIED);
            }
        }
        else{
            return ResponseEntity.notFound().build();
        }

        if(!(photo==null && photo.isEmpty())){
            if (photo.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Photo size must be less than 5MB");
            }
            String contentType = photo.getContentType();
            if (contentType == null || !contentType.matches("image/(jpeg|png)")) {
                return ResponseEntity.badRequest().body("Only JPEG and PNG allowed");
            }
        }
        if (userProfile.getDateOfBirth() != null) {
            int age = Period.between(userProfile.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 16) {
                return ResponseEntity.badRequest().body("Age must be at least 16");
            }
        }
        userProfileService.saveProfile(userProfile, photo, currentUserDTO.getId());
        return ResponseEntity.ok(Map.of("message", "Profile saved successfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable  String userId){
        UserResponseDTO userDto=userService.getUserById(userId);
        UserResponseDTO currentUserDto=securityUtil.getCurrentUserDto();
        if(userDto!=null && currentUserDto!=null){
            if(!(userDto.getId().equals(currentUserDto.getId()))){
                throw  new ResponseStatusException(HttpStatus.FORBIDDEN,Constants.ACCESS_DENIED);
            }
        }
        else{
            return ResponseEntity.notFound().build();
        }
        UserProfileDTO userProfileDTO=userProfileService.getUserProfileById(userId);
        if(userProfileDTO!=null){
            return ResponseEntity.ok(userProfileDTO);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/explore")
    public ResponseEntity<?> getOthersProfile(@RequestParam(defaultValue = "10")int limit){
        // get the current user
        UserResponseDTO currentUserDto=securityUtil.getCurrentUserDto();
        if(currentUserDto==null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,Constants.ACCESS_DENIED);
        }
        //fetch current user profile
        UserProfileDTO currentUserProfile=userProfileService.getUserProfileById(currentUserDto.getId());
        if(currentUserProfile==null){
            return ResponseEntity.badRequest().body("Create your profile first to explore others");
        }
        if(currentUserProfile.getPrimaryDomain()==null){
            return ResponseEntity.badRequest().body("Set your primary domain First");
        }
        List<UserProfileDTO> userProfileDTOList=userProfileService.getOtherProfiles(currentUserDto.getId(), limit);
        return ResponseEntity.ok(userProfileDTOList);
    }
}
