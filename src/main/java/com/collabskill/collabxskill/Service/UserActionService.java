package com.collabskill.collabxskill.Service;


import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.Service.extra.CollabReceivedDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface UserActionService {
    Map<String,String> handleSwipeAction(String fromUserId, String toUserId, String action,String message);

    Page<CollabReceivedDTO> getCollabReceived(String id, int page, int size);

    Page<CollabReceivedDTO>  getCollabSent(String id, int page, int size);

    Map<String,String> blockUser(String id, String userId);

    Map<String,String> unBlockUser(String id, String userId);


}
