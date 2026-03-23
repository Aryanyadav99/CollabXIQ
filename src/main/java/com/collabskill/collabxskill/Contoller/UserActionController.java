package com.collabskill.collabxskill.Contoller;

import com.collabskill.collabxskill.Entities.UserAction;
import com.collabskill.collabxskill.Service.UserActionService;
import com.collabskill.collabxskill.io.UserResponseDTO;
import com.collabskill.collabxskill.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user-actinos")
@RequiredArgsConstructor
public class UserActionController {
    private final SecurityUtil securityUtil;
    private final UserActionService userActionService;

    @PostMapping("/{toUserId}")
    public ResponseEntity<?> swipeAction(@PathVariable String toUserId,
                                         @RequestParam String Action,
                                         @RequestParam(required = false) String message){
        UserResponseDTO userResponseDTO=securityUtil.getCurrentUserDto();
        if(userResponseDTO==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login First");
        }
        Map<String,String>response=userActionService.handleSwipeAction(userResponseDTO.getId(),toUserId,Action,message);
        return null;
    }
    @GetMapping("/received")
    public ResponseEntity<?> getCollabReceived(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UserResponseDTO currentUser = securityUtil.getCurrentUserDto();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login karo pehle");
        }

        return ResponseEntity.ok(
                userActionService.getCollabReceived(currentUser.getId(), page, size));
    }
}
