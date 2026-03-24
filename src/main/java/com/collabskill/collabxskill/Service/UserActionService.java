package com.collabskill.collabxskill.Service;


import com.collabskill.collabxskill.Service.extra.CollabReceivedDTO;

import java.util.List;
import java.util.Map;

public interface UserActionService {
    Map<String,String> handleSwipeAction(String fromUserId, String toUserId, String action,String message);

    List<CollabReceivedDTO> getCollabReceived(String id, int page, int size);

    List<CollabReceivedDTO>  getCollabSent(String id, int page, int size);

    Map<String,String> blockUser(String id, String userId);

    Map<String,String> unBlockUser(String id, String userId);
}
