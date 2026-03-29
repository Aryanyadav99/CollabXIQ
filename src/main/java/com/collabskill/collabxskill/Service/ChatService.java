package com.collabskill.collabxskill.Service;

import com.collabskill.collabxskill.io.ChatMessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.messaging.Message;

public interface ChatService {
    void sendMessage(ChatMessageDTO messageDTO, Message<?> stompMessage);

    Page<ChatMessageDTO> getHistory(String user1, String user2, int page, int size);
}
