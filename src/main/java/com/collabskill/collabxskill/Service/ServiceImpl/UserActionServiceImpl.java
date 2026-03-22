package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.Entities.UserAction;
import com.collabskill.collabxskill.Service.UserActionService;
import com.collabskill.collabxskill.extra.ActionType;
import com.collabskill.collabxskill.repo.UserActionRepository;
import com.collabskill.collabxskill.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserRepository userRepository;
    private final UserActionRepository userActionRepository;

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