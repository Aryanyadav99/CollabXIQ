package com.collabskill.collabxskill.repo;

import com.collabskill.collabxskill.Entities.UserAction;
import com.collabskill.collabxskill.extra.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    UserAction findByFromUserIdAndToUserId(String fromUserId, String toUserId);

    Optional<UserAction> findByFromUser_IdAndToUser_IdAndActionTypeAndCreatedAtAfter(String toUserId, String fromUserId, ActionType actionType, LocalDateTime localDateTime);

    long countByFromUser_IdAndActionTypeAndCreatedAtAfter(String fromUserId, ActionType actionType, LocalDateTime localDateTime);

    Optional<UserAction> findByFromUser_IdAndToUser_Id(String toUserId, String fromUserId);

    boolean existsByFromUser_IdAndToUser_IdAndActionType(String toUserId, String fromUserId, ActionType actionType);

    boolean existsByFromUser_IdAndToUser_IdAndActionTypeIn(String toUserId, String fromUserId, List<ActionType> collab);
}
