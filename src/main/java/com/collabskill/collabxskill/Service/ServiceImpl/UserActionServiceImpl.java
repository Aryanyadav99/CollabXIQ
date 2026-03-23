package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.Entities.UserAction;
import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.Service.UserActionService;
import com.collabskill.collabxskill.Service.extra.CollabReceivedDTO;
import com.collabskill.collabxskill.extra.ActionType;
import com.collabskill.collabxskill.extra.UserProfileDTO;
import com.collabskill.collabxskill.io.UserResponseDTO;
import com.collabskill.collabxskill.repo.UserActionRepository;
import com.collabskill.collabxskill.repo.UserRepository;
import com.collabskill.collabxskill.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserRepository userRepository;
    private final UserActionRepository userActionRepository;
    private final ModelMapper  modelMapper;
    @Override
    public Map<String, String> handleSwipeAction(String fromUserId, String toUserId, String actionType, String message) {
        User fromUser = userRepository.findById(fromUserId).orElseThrow(() -> new RuntimeException("User not found"));
        User toUser = userRepository.findById(toUserId).orElseThrow(() -> new RuntimeException("User not found"));
        ActionType action = ActionType.valueOf(actionType.toUpperCase());

        // first check is fromUser blocked toUser
        boolean isBlocked = userActionRepository
                .existsByFromUser_IdAndToUser_IdAndActionType(
                        toUserId, fromUserId, ActionType.BLOCK);
        if (isBlocked) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Action not allowed");
        }

        // check if fromUser already reject toUser and time limit of 7 day is not exceed
        Optional<UserAction> recentReject = userActionRepository
                .findByFromUser_IdAndToUser_IdAndActionTypeAndCreatedAtAfter(
                        toUserId, fromUserId, ActionType.REJECT,
                        LocalDateTime.now().minusDays(7));
        if (recentReject.isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you cannot send the request now");
        }

        // check superCollab daily limit -- only 3 per day
        if (action == ActionType.SUPER_COLLAB) {
            if (message == null || message.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Message cannot be null in SuperCollab");
            }
            long todayCount = userActionRepository
                    .countByFromUser_IdAndActionTypeAndCreatedAtAfter(
                            fromUserId, ActionType.SUPER_COLLAB,
                            LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));
            if (todayCount >= 3) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "SuperCollab Limit Exceed take premium(avail soon)");
            }
        }

        // check if already present update that action !
        Optional<UserAction> existing = userActionRepository
                .findByFromUser_IdAndToUser_Id(fromUserId, toUserId);

        UserAction userAction;
        if (existing.isPresent()) {
            userAction = existing.get(); // update karo
        } else {
            userAction = new UserAction(); // naya banao
            userAction.setFromUser(fromUser);
            userAction.setToUser(toUser);
        }

        userAction.setActionType(action);
        if (action == ActionType.SUPER_COLLAB) {
            userAction.setMessage(message);
        }
        userActionRepository.save(userAction);

        // Match if there is Collab or SuperCollab and opposite user also send collab or super collab
        if (action == ActionType.COLLAB || action == ActionType.SUPER_COLLAB) {
            boolean matched = checkMatch(fromUserId, toUserId);
            if (matched) {
                return Map.of("message", "MATCHED! Chat Unlocked ");
            }
            return Map.of("message", "COLLAB sent - waiting for response");
        }

        return Map.of("message", action + " successful");
    }

    @Override
    public List<CollabReceivedDTO> getCollabReceived(String userId, int page, int size) {

        List<UserAction> actions = userActionRepository
                .findByToUser_IdAndActionTypeIn(
                        userId,
                        List.of(ActionType.COLLAB, ActionType.SUPER_COLLAB),
                        PageRequest.of(page, size));

        return actions.stream()
                .map(action -> {
                    CollabReceivedDTO dto = new CollabReceivedDTO();
                    UserProfile profile = action.getFromUser().getUserProfile();
                    dto.setProfile(modelMapper.map(profile, UserProfileDTO.class));
                    dto.setActionType(action.getActionType().name());
                    dto.setMessage(action.getMessage());
                    dto.setCreatedAt(action.getCreatedAt());
                    return dto;
                })
                .sorted(Comparator.comparing(dto ->
                        dto.getActionType().equals("SUPER_COLLAB") ? 0 : 1)) // first  SUPER_COLLAB
                .toList();
    }
    @Override
    public List<CollabReceivedDTO> getCollabSent(String userId, int page, int size) {

        List<UserAction> actions = userActionRepository
                .findByFromUser_IdAndActionTypeIn(
                        userId,
                        List.of(ActionType.COLLAB, ActionType.SUPER_COLLAB),
                        PageRequest.of(page, size));

        return actions.stream()
                .map(action -> {
                    CollabReceivedDTO dto = new CollabReceivedDTO();

                    // toUser ka profile — jisko tune bheja
                    UserProfile profile = action.getToUser().getUserProfile();
                    dto.setProfile(modelMapper.map(profile, UserProfileDTO.class));

                    dto.setActionType(action.getActionType().name());
                    dto.setMessage(action.getMessage());
                    dto.setCreatedAt(action.getCreatedAt());
                    return dto;
                })
                .sorted(Comparator.comparing(dto ->
                        dto.getActionType().equals("SUPER_COLLAB") ? 0 : 1))
                .collect(Collectors.toList());
    }

    private boolean checkMatch(String fromUserId, String toUserId) {

        // Kya samne wale ne bhi COLLAB ya SUPER_COLLAB kiya hai?
        boolean reciprocalExists = userActionRepository
                .existsByFromUser_IdAndToUser_IdAndActionTypeIn(
                        toUserId, fromUserId,
                        List.of(ActionType.COLLAB, ActionType.SUPER_COLLAB));

        if (reciprocalExists) {
            // Dono ka action MATCHED mein update karo
            Optional<UserAction> myAction = userActionRepository
                    .findByFromUser_IdAndToUser_Id(fromUserId, toUserId);

            Optional<UserAction> theirAction = userActionRepository
                    .findByFromUser_IdAndToUser_Id(toUserId, fromUserId);

            myAction.ifPresent(action -> {
                action.setActionType(ActionType.MATCHED);
                userActionRepository.save(action);
            });

            theirAction.ifPresent(action -> {
                action.setActionType(ActionType.MATCHED);
                userActionRepository.save(action);
            });

            return true; // MATCHED :>
        }

        return false; // wait for response :
    }

}