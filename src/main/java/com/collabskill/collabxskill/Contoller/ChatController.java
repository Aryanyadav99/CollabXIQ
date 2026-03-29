package com.collabskill.collabxskill.Contoller;

import com.collabskill.collabxskill.Service.ChatService;
import com.collabskill.collabxskill.io.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    @MessageMapping("/send")
    public void sendMessage(@Payload  ChatMessageDTO chatMessage, Message<?> stompMessage) {
        chatService.sendMessage(chatMessage,stompMessage);
    }
}
