package com.collabskill.collabxskill.Contoller;

import com.collabskill.collabxskill.Entities.UserAction;
import com.collabskill.collabxskill.Service.UserActionService;
import com.collabskill.collabxskill.io.CollabReceivedDTO;
import com.collabskill.collabxskill.io.UserProfileDTO;
import com.collabskill.collabxskill.io.UserResponseDTO;
import com.collabskill.collabxskill.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/user-actions")
@RequiredArgsConstructor
public class UserActionController {
    private final SecurityUtil securityUtil;
    private final UserActionService userActionService;

    @PostMapping("/{toUserId}")
    public ResponseEntity<?> swipeAction(@PathVariable String toUserId,
                                         @RequestParam String actionType,
                                         @RequestParam(required = false) String message){
        UserResponseDTO userResponseDTO=securityUtil.getCurrentUserDto();
        if(userResponseDTO==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login First");
        }
        Map<String,String>response=userActionService.handleSwipeAction(userResponseDTO.getId(),toUserId,actionType,message);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/received")
    public Page<CollabReceivedDTO> getCollabReceived(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UserResponseDTO currentUser = securityUtil.getCurrentUserDto();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Login First") ;
        }

        return userActionService.getCollabReceived(currentUser.getId(), page, size);
    }

    @GetMapping("/sent")
    public Page<CollabReceivedDTO> getCollabSent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UserResponseDTO currentUser = securityUtil.getCurrentUserDto();
        if (currentUser == null) {
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Login First") ;
        }

        return userActionService.getCollabSent(currentUser.getId(), page, size);
    }

    @PostMapping("/block/{userId}")
    public ResponseEntity<?> blockUser(@PathVariable String userId) throws ResponseStatusException {
        UserResponseDTO currentUser = securityUtil.getCurrentUserDto();
        if(currentUser==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Login First");
        if(currentUser.getId().equals(userId)) throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cannot block yoursef");

        return ResponseEntity.ok(userActionService.blockUser(currentUser.getId(),userId));

    }

    @PostMapping("/unblock/{userId}")
    public ResponseEntity<?> unBlockUser(@PathVariable String userId){
        UserResponseDTO currentUser=securityUtil.getCurrentUserDto();
        if(currentUser==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Login First");
        return ResponseEntity.ok(userActionService.unBlockUser(currentUser.getId(),userId));
    }

    @GetMapping("/matches")
    public Page<UserProfileDTO> getMatches(@RequestParam(defaultValue = "0")int page,
                                           @RequestParam(defaultValue = "10") int size){
        UserResponseDTO userResponseDTO=securityUtil.getCurrentUserDto();
        if(userResponseDTO==null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Login First");
        }
        return userActionService.getYourMathces(userResponseDTO.getId(),page,size);
    }

    @PostMapping("/{fromUserId}/accept")
    public ResponseEntity<?> acceptCollab(@PathVariable String fromUserId) {

        UserResponseDTO currentUser = securityUtil.getCurrentUserDto();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login First");
        }

        return ResponseEntity.ok(
                userActionService.acceptCollab(currentUser.getId(), fromUserId));
    }

    @PostMapping("/{fromUserId}/reject")
    public ResponseEntity<?> rejectCollab(@PathVariable String fromUserId) {

        UserResponseDTO currentUser = securityUtil.getCurrentUserDto();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login First");
        }

        return ResponseEntity.ok(
                userActionService.rejectCollab(currentUser.getId(), fromUserId));
    }
}
