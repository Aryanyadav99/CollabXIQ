package com.collabskill.collabxskill.repo;

import com.collabskill.collabxskill.Entities.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepo extends JpaRepository<ChatMessage, String> {

    @Query("""
        SELECT c
        FROM ChatMessage c
        WHERE
        (c.senderId = :user1 AND c.receiverId = :user2)
        OR
        (c.senderId = :user2 AND c.receiverId = :user1)
    """)
    Page<ChatMessage> getChatMessages(
            @Param("user1") String user1,
            @Param("user2") String user2,
            Pageable pageable
    );
}