package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.Entities.UserAction;
import com.collabskill.collabxskill.Entities.UserProfile;
import com.collabskill.collabxskill.Service.UserActionService;
import com.collabskill.collabxskill.io.CollabReceivedDTO;
import com.collabskill.collabxskill.extra.ActionType;
import com.collabskill.collabxskill.io.UserProfileDTO;
import com.collabskill.collabxskill.repo.UserActionRepository;
import com.collabskill.collabxskill.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<CollabReceivedDTO> getCollabReceived(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("actionType"))); // SUPER_COLLAB pehle

        Page<UserAction> actions = userActionRepository
                .findByToUser_IdAndActionTypeIn(
                        userId,
                        List.of(ActionType.COLLAB, ActionType.SUPER_COLLAB),
                        pageable);

        return actions.map(action -> {
            CollabReceivedDTO dto = new CollabReceivedDTO();
            UserProfile profile = action.getFromUser().getUserProfile();
            dto.setProfile(modelMapper.map(profile, UserProfileDTO.class));
            dto.setActionType(action.getActionType().name());
            dto.setMessage(action.getMessage());
            dto.setCreatedAt(action.getCreatedAt());
            return dto;
        });
    }





    @Override
    public Page<CollabReceivedDTO> getCollabSent(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("actionType")));

        Page<UserAction> actions = userActionRepository
                .findByFromUser_IdAndActionTypeIn(
                        userId,
                        List.of(ActionType.COLLAB, ActionType.SUPER_COLLAB),
                        pageable);

        return actions.map(action -> {
            CollabReceivedDTO dto = new CollabReceivedDTO();
            UserProfile profile = action.getToUser().getUserProfile();
            dto.setProfile(modelMapper.map(profile, UserProfileDTO.class));
            dto.setActionType(action.getActionType().name());
            dto.setMessage(action.getMessage());
            dto.setCreatedAt(action.getCreatedAt());
            return dto;
        });
    }




    @Override
    public Map<String,String> blockUser(String id, String userId) {
        User fromUser= userRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        User toUser= userRepository.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        boolean isBlocked=userActionRepository.existsByFromUser_IdAndToUser_IdAndActionType(
                id,userId, ActionType.BLOCK
        );
        if(isBlocked){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Already Blocked");
        }
        UserAction blockAction;
        Optional<UserAction> fromUserAction = userActionRepository.findByFromUser_IdAndToUser_Id(id,userId);
        Optional<UserAction> toUserAction=userActionRepository.findByFromUser_IdAndToUser_Id(userId,id);

        if(fromUserAction.isPresent()){
            blockAction=fromUserAction.get();
        }
        else{
            blockAction=new UserAction();
            blockAction.setFromUser(fromUser);
            blockAction.setToUser(toUser);
        }
        blockAction.setActionType(ActionType.BLOCK);
        blockAction.setMessage(null); // clear the message if any
        userActionRepository.save(blockAction);
        // Reset the action of another person to COLLAB if it was MATCHED
        toUserAction.ifPresent(action -> {
            if (action.getActionType() == ActionType.MATCHED) {
                action.setActionType(ActionType.COLLAB);
                userActionRepository.save(action);
            }
        });
        return Map.of("message", "User blocked successfully");
    }





    @Override
    public Map<String, String> unBlockUser(String id, String userId) {
        User currentUser= userRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        User toUser= userRepository.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        boolean blocked = userActionRepository.existsByFromUser_IdAndToUser_IdAndActionType(id,userId,ActionType.BLOCK);
        if(!blocked){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User is not blocked");
        }
        Optional<UserAction> blockAction = userActionRepository
                .findByFromUser_IdAndToUser_Id(id, userId);

        blockAction.ifPresent(action -> {
            action.setActionType(ActionType.MATCHED);
            userActionRepository.save(action);
        });


        Optional<UserAction> theirAction = userActionRepository
                .findByFromUser_IdAndToUser_Id(userId, id);

        theirAction.ifPresent(action -> {
            action.setActionType(ActionType.MATCHED);
            userActionRepository.save(action);
        });

        return Map.of("message", "User unblocked successfully");
    }




    @Override
    public Page<UserProfileDTO> getYourMathces(String id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<UserAction> actions = userActionRepository
                .findByFromUser_IdAndActionType(
                        id,
                        ActionType.MATCHED,
                        pageable);

        return actions.map(action -> {
            UserProfile profile = action.getToUser().getUserProfile();
            return modelMapper.map(profile, UserProfileDTO.class);
        });
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