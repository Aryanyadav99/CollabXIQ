package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Entities.ChatMessage;
import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.Service.ChatService;
import com.collabskill.collabxskill.extra.Constants;
import com.collabskill.collabxskill.io.ChatMessageDTO;
import com.collabskill.collabxskill.repo.ChatRepo;
import com.collabskill.collabxskill.repo.UserProfileRepo;
import com.collabskill.collabxskill.repo.UserRepository;
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
    private final UserRepository userRepo;
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

        String originalReceiverId = message.getReceiverId();

        message.setSenderId(currentUserId);
        String normalizedReceiverId =
                normalizeToUserId(message.getReceiverId());

        message.setReceiverId(normalizedReceiverId);

        ChatMessage save = chatRepo.save(message);

        messagingTemplate.convertAndSend("/topic/" + originalReceiverId, save);
        System.out.println(
                "Sending to topic: /topic/" + message.getReceiverId()
        );
        //NOTE: "Send message only to receiver via WebSocket, sender sees it instantly through frontend optimistic UI update"
    }

    @Override
    public Page<ChatMessageDTO> getHistory(String user1, String user2, int page, int size) {
        User currentUser = securityUtil.getCurrentUser();

        if (!currentUser.getId().equals(user1)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, Constants.ACCESS_DENIED);
        }
        String normalizedUser2 =
                normalizeToUserId(user2);


        Page<ChatMessage> chatMessages =
                chatRepo.getChatMessages(
                        user1,
                        normalizedUser2,
                        PageRequest.of(
                                page,
                                size,
                                Sort.by(
                                        Sort.Order.asc("timestamp"),
                                        Sort.Order.asc("id")
                                )
                        )
                );

        return chatMessages.map(msg -> modelMapper.map(msg, ChatMessageDTO.class));
    }
    private String normalizeToUserId(String incomingId) {

        // agar already userId hai
        if (userRepo.existsById(incomingId)) {
            return incomingId;
        }

        // warna profileId hoga
        UserProfile profile = userProfileRepository
                .findById(incomingId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Not found"
                        )
                );

        return profile.getUser().getId();
    }
}
