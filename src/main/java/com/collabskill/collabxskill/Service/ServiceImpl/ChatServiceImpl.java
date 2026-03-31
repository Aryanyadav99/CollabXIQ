package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Entities.ChatMessage;
import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.Service.ChatService;
import com.collabskill.collabxskill.extra.Constants;
import com.collabskill.collabxskill.io.ChatMessageDTO;
import com.collabskill.collabxskill.repo.ChatRepo;
import com.collabskill.collabxskill.repo.UserProfileRepo;
import com.collabskill.collabxskill.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ModelMapper modelMapper;
    private final UserProfileRepo userProfileRepository;
    private final ChatRepo chatRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityUtil securityUtil;

    @Override
    public void sendMessage(ChatMessageDTO messageDTO, Message<?> stompMessage) {

        ChatMessage message = modelMapper.map(messageDTO, ChatMessage.class);

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(stompMessage);
        System.out.println("Sender from DTO: " + message.getSenderId());
        if(accessor.getSessionAttributes() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String currentUserId = (String) accessor.getSessionAttributes().get("userId");

        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

// always trust backend
        message.setSenderId(currentUserId);

        ChatMessage save = chatRepo.save(message);

        messagingTemplate.convertAndSend("/topic/" + message.getReceiverId(), save);
        //NOTE: "Send message only to receiver via WebSocket, sender sees it instantly through frontend optimistic UI update"
    }

    @Override
    public Page<ChatMessageDTO> getHistory(String user1, String user2, int page, int size) {
        User currentUser = securityUtil.getCurrentUser();

        if (!currentUser.getId().equals(user1) && !currentUser.getId().equals(user2)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, Constants.ACCESS_DENIED);
        }

        Optional<UserProfile> targetProfile = userProfileRepository.findById(user2);
        if (targetProfile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found");
        }

        User toUser = targetProfile.get().getUser();
        user2 = (toUser.getId());

        Page<ChatMessage> chatMessages = chatRepo.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(user1, user2,
                user1, user2, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")));

        return chatMessages.map(msg -> modelMapper.map(msg, ChatMessageDTO.class));
    }
}
