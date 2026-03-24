package com.collabskill.collabxskill.io;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollabReceivedDTO {

    private UserProfileDTO profile;
    private String actionType;   // COLLAB ya SUPER_COLLAB
    private String message;      // only for SuperCollab
    private LocalDateTime createdAt;
}
