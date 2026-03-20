package com.collabskill.collabxskill.extra;

public enum ActionType {
    COLLAB, // when user swipe left it act as collab but dint send the req if both do collab it will be matched and chat opened
    SKIP,// user not interested for now so just skip and the other user didnt came for 7 day in user profile
    SUPER_COLLAB,//super collab means heavy interest and ready to connect so it send the request to other user also
    MATCHED, // chat opened
    REJECT,// reject request so the user is acted like a block for 7 day and after 7 day he/she can again send the request
    BLOCK // permanently block the user and it cannot be connected or shown to that user untill he unblock the user .
}
