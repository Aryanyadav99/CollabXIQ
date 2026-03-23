package com.collabskill.collabxskill.Service;


import java.util.Map;

public interface UserActionService {
    Map<String,String> handleSwipeAction(String fromUserId, String toUserId, String action,String message);

    Object getCollabReceived(String id, int page, int size);
}
