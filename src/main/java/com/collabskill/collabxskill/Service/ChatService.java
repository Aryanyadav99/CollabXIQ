package com.collabskill.collabxskill.Service;

import com.collabskill.collabxskill.io.ChatMessageDTO;
import org.springframework.messaging.Message;

public interface ChatService {
    void sendMessage(ChatMessageDTO messageDTO, Message<?> stompMessage);
}
