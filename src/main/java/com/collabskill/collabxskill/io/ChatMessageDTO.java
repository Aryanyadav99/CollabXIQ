package com.collabskill.collabxskill.io;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String timestamp;
}
