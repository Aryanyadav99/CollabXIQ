package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Entities.ChatMessage;
import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.Service.ChatService;
import com.collabskill.collabxskill.io.ChatMessageDTO;
import com.collabskill.collabxskill.repo.ChatRepo;
import com.collabskill.collabxskill.repo.UserProfileRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ModelMapper modelMapper;
    private final UserProfileRepo userProfileRepository;
    private final ChatRepo chatRepo;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessage(ChatMessageDTO messageDTO, Message<?> stompMessage) {
        ChatMessage message = modelMapper.map(messageDTO, ChatMessage.class);

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(stompMessage);
        if(accessor.getSessionAttributes() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String currentUserId = (String) accessor.getSessionAttributes().get("userId");

        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        if (!message.getSenderId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot send messages as another user");
        }

        Optional<UserProfile> targetProfile = userProfileRepository.findById(message.getReceiverId());
        if (targetProfile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found");
        }

        User toUser = targetProfile.get().getUser();
        message.setReceiverId(toUser.getId());

        ChatMessage save = chatRepo.save(message);
        messagingTemplate.convertAndSend("/topic/" + message.getReceiverId(), save);
        //NOTE: "Send message only to receiver via WebSocket, sender sees it instantly through frontend optimistic UI update"
    }
}
