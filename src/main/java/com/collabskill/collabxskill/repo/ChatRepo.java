package com.collabskill.collabxskill.repo;

import com.collabskill.collabxskill.Entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepo extends JpaRepository<ChatMessage,String> {
}
