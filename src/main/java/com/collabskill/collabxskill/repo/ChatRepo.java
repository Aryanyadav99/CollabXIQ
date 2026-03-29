package com.collabskill.collabxskill.repo;

import com.collabskill.collabxskill.Entities.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepo extends JpaRepository<ChatMessage,String> {
    Page<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(String senderId, String receiverId,
                                                                         String receiverId2, String senderId2, Pageable pageable);
}
